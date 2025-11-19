package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.TransactionCreateDto;
import com.example.muscle_market.dto.TransactionStatusUpdateDto;
import com.example.muscle_market.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    // 거래 상태 만들고 예약으로 설정
    @PostMapping
    public ResponseEntity<Long> createTransaction(@RequestBody TransactionCreateDto request) {
        Long transactionId = transactionService.createTransaction(request);

        // 201 Created 응답과 생성된 리소스의 ID 반환
        return new ResponseEntity<>(transactionId, HttpStatus.CREATED);
    }

    // 판매자가 원하는 거래 상태 변경
    @PatchMapping("/{transactionId}/status")
    public ResponseEntity<Long> updateTransactionStatus(
            @PathVariable Long transactionId,
            @RequestBody TransactionStatusUpdateDto request) {

        Long updatedId = transactionService.updateTransactionStatus(transactionId, request);

        return ResponseEntity.ok(updatedId);
    }
}
