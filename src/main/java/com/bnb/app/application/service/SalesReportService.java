package com.bnb.app.application.service;

import com.bnb.app.domain.model.OrderLineEntity;
import com.bnb.app.domain.repository.OrderLineRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SalesReportService {

    private final OrderLineRepository orderLineRepository;

    public SalesReportService(OrderLineRepository orderLineRepository) {
        this.orderLineRepository = orderLineRepository;
    }

    public byte[] exportSalesReport(LocalDate date) {
        Instant start = startOfDay(date);
        Instant end = endOfDay(date);
        List<OrderLineEntity> orderLines = orderLineRepository.findByCreatedAtBetween(start, end);

        List<ItemSales> sales = aggregateSales(orderLines);
        BigDecimal totalAmount = sales.stream()
                .map(ItemSales::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Items Sold");
            createHeaderRow(workbook, sheet);
            writeSalesRows(sheet, sales);
            writeTotalRow(workbook, sheet, sales.size() + 1, totalAmount);
            autosizeColumns(sheet);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to build sales report", ex);
        }
    }

    private List<ItemSales> aggregateSales(List<OrderLineEntity> orderLines) {
        Map<ItemKey, Integer> quantityByItem = orderLines.stream()
                .collect(Collectors.toMap(
                        line -> new ItemKey(
                                line.getItemNameSnapshot(),
                                line.getCategoryNameSnapshot(),
                                line.getUnitPriceSnapshot()),
                        OrderLineEntity::getQuantity,
                        Integer::sum
                ));

        return quantityByItem.entrySet().stream()
                .map(entry -> new ItemSales(
                        entry.getKey().itemName(),
                        entry.getKey().categoryName(),
                        entry.getKey().unitPrice(),
                        entry.getValue()))
                .toList();
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setWrapText(true);

        String[] headers = {"Item", "Category", "Unit Price", "Quantity Sold", "Line Total"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeSalesRows(Sheet sheet, List<ItemSales> sales) {
        for (int index = 0; index < sales.size(); index++) {
            ItemSales sale = sales.get(index);
            Row row = sheet.createRow(index + 1);
            row.createCell(0).setCellValue(sale.itemName());
            row.createCell(1).setCellValue(sale.categoryName());
            row.createCell(2).setCellValue(sale.unitPrice().doubleValue());
            row.createCell(3).setCellValue(sale.quantity());
            row.createCell(4).setCellValue(sale.total().doubleValue());
        }
    }

    private void writeTotalRow(Workbook workbook, Sheet sheet, int rowIndex, BigDecimal totalAmount) {
        Row totalRow = sheet.createRow(rowIndex);
        totalRow.createCell(3).setCellValue("Total Amount");
        Cell totalCell = totalRow.createCell(4);
        totalCell.setCellValue(totalAmount.doubleValue());

        CellStyle currencyStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        currencyStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00"));
        totalCell.setCellStyle(currencyStyle);
    }

    private void autosizeColumns(Sheet sheet) {
        for (int columnIndex = 0; columnIndex < 5; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    private Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    private Instant endOfDay(LocalDate date) {
        return date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant();
    }

    private record ItemKey(String itemName, String categoryName, BigDecimal unitPrice) {
    }

    private record ItemSales(String itemName, String categoryName, BigDecimal unitPrice, int quantity) {
        BigDecimal total() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
