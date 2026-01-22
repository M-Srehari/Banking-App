package com.example.bankApplication.controller;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import com.lowagie.text.Document;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.bankApplication.Entity.TransactionEntity;
import com.example.bankApplication.dto.BankDTO;
import com.example.bankApplication.dto.TransactionDTO;
import com.example.bankApplication.dto.UserResponseDTO;
import com.example.bankApplication.service.BankService;
import com.example.bankApplication.service.TransactionHistoryService;
import com.lowagie.text.PageSize;


import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

import java.awt.Color;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;



@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class BankController {

    @Autowired
    private BankService bankService;
    
	@Autowired
	private TransactionHistoryService historyService;

    @PostMapping("/saveusers")
    public String createUser(@RequestBody BankDTO dto) {
        try {
            return bankService.saveUser(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Saving" + e.getMessage();
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody BankDTO dto) {
        try {
            return bankService.login(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error login: " + e.getMessage();
        }
    }

    @PostMapping("/validate-otp")
    public String validateOtp(@RequestBody BankDTO dto) {
        try {
            return bankService.validateOtp(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sendig OTP " + e.getMessage();
        }
    }
    
    @GetMapping("/user")
    public ResponseEntity<UserResponseDTO> getUser(@RequestParam(required = false) Long id,@RequestParam(required = false) String email) {

        try {
            UserResponseDTO dto = bankService.getUserData(Optional.ofNullable(id), Optional.ofNullable(email));
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }
    
    @PostMapping("/addmoney")
    public String deposit(@RequestBody TransactionDTO transaction) {
        try {
            return bankService.depositMoney(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Adding Money " + e.getMessage();
        }
    }

    @PostMapping("/sendamt")
    public String sendMoney(@RequestBody TransactionDTO transaction) {
        try {
            return bankService.sendMoney(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Sending Money " + e.getMessage();
        }
    }
    
    @GetMapping("/download/csv")
    public void downloadTransactionsCsv(@RequestParam String accountNumber, HttpServletResponse response) {

        try {
            response.setContentType("text/csv");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=transactions_" + accountNumber + ".csv"
            );

            List<TransactionEntity> transactions =
                    historyService.getTransactions(accountNumber);

            try (PrintWriter writer = response.getWriter()) {

                writer.println("ID,From,To,Amount,Type,Date,BalanceAfter");

                transactions.stream().forEach(tx ->
                        writer.println(
                                tx.getId() + "," +
                                tx.getFromAccount() + "," +
                                tx.getToAccount() + "," +
                                tx.getAmount() + "," +
                                tx.getType() + "," +
                                tx.getTransactionDate() + "," +
                                tx.getBalanceAfter()
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
//    @GetMapping("/download/pdf")
//    public void downloadTransactionsPdf(@RequestParam String accountNumber, HttpServletResponse response) {
//        try {
//            response.setContentType("application/pdf");
//            response.setHeader(
//                "Content-Disposition",
//                "attachment; filename=transactions_" + accountNumber + ".pdf"
//            );
//
//            List<TransactionEntity> transactions = historyService.getTransactions(accountNumber);
//
//            Document document = new Document(PageSize.A4);
//            PdfWriter.getInstance(document, response.getOutputStream());
//
//            document.open();
//
//            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
//            Paragraph title = new Paragraph("Transaction History", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//
//            document.add(new Paragraph("Account Number: " + accountNumber));
//            document.add(new Paragraph(" "));
//
//            PdfPTable table = new PdfPTable(7);
//            table.setWidthPercentage(100);
//
//            Stream.of("ID", "From", "To", "Amount", "Type", "Date", "Balance")
//                .forEach(header -> {
//                    PdfPCell cell = new PdfPCell();
//                    cell.setPhrase(new Phrase(header));
//                    cell.setBackgroundColor(Color.LIGHT_GRAY);
//                    table.addCell(cell);
//                });
//
//            transactions.forEach(tx -> {
//                table.addCell(String.valueOf(tx.getId()));
//                table.addCell(tx.getFromAccount());
//                table.addCell(tx.getToAccount());
//                table.addCell(String.valueOf(tx.getAmount()));
//                table.addCell(tx.getType());
//                table.addCell(String.valueOf(tx.getTransactionDate()));
//                table.addCell(String.valueOf(tx.getBalanceAfter()));
//            });
//
//            document.add(table);
//            document.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//    }
    
    @GetMapping("/download/pdf")
    public void downloadTransactionsPdf(@RequestParam String accountNumber,@RequestParam String fromDate, @RequestParam String toDate,HttpServletResponse response) {

        try {
            LocalDateTime fromDateTime = LocalDate.parse(fromDate).atStartOfDay();

            LocalDateTime toDateTime = LocalDate.parse(toDate).atTime(23, 59, 59);

            List<TransactionEntity> transactions =
                    historyService.getTransactionsBetweenDates(accountNumber, fromDateTime, toDateTime);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition","attachment; filename=transactions.pdf");

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont =FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

            Paragraph title =new Paragraph("Transaction History", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Account Number: " + accountNumber));
            document.add(new Paragraph("From: " + fromDate + " To: " + toDate));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            Stream.of("ID", "From", "To", "Amount", "Type", "Date", "Balance")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        table.addCell(cell);
                    });

            for (TransactionEntity tx : transactions) {
                table.addCell(String.valueOf(tx.getId()));
                table.addCell(tx.getFromAccount());
                table.addCell(tx.getToAccount());
                table.addCell(String.valueOf(tx.getAmount()));
                table.addCell(tx.getType());
                table.addCell(String.valueOf(tx.getTransactionDate()));
                table.addCell(String.valueOf(tx.getBalanceAfter()));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/resendOtp")
    public ResponseEntity<Object> resendOtp(@RequestBody BankDTO dto) {
        return ResponseEntity.ok(bankService.resendOtp(dto.getEmail()));
    }
}