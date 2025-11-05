package com.example.muscle_market.service;

import com.example.muscle_market.domain.Product;
import com.example.muscle_market.domain.Transaction;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.domain.enums.TransactionStatus;
import com.example.muscle_market.dto.TransactionCreateDto;
import com.example.muscle_market.dto.TransactionStatusUpdateDto;
import com.example.muscle_market.repository.ProductRepository;
import com.example.muscle_market.repository.TransactionRepository;
import com.example.muscle_market.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 거래 예약 설정
    // 거래 상태를 Product, Transaction 엔티티에 두개 다 생성함
    // 둘다 적용해 줌
    @Transactional
    public Long createTransaction(TransactionCreateDto request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다."));

        // 판매자와 구매자 확인
        User seller = product.getUser(); // Product에 연결된 User가 판매자
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("구매자를 찾을 수 없습니다."));

        // 자기 게시물의 예약 금지 ui 뚫어놓지도 않을 거지만
        if (seller.getId().equals(customer.getId())) {
            throw new IllegalStateException("자신의 상품을 예약할 수 없습니다.");
        }

        // 상품의 거래 상태를 예약중으로 변경
        if (product.getStatus() != TransactionStatus.SELLING) {
            throw new IllegalStateException("이미 거래 중이거나 예약된 상품입니다. 현재 상태: " + product.getStatus().getDescription());
        }
        product.updateStatus(TransactionStatus.RESERVED);
        // productRepository.save(product);

        // Transaction 엔티티 생성 및 저장
        Transaction transaction = Transaction.builder()
                .product(product)
                .seller(seller)
                .customer(customer)
                .location(request.getLocation())
                .date(request.getTransactionDate())
                .status(TransactionStatus.RESERVED) // 초기 상태는 예약중
                // createdAt/updatedAt은 자동 처리
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return savedTransaction.getId();
    }

    // 거래 상태 변경
    // Transaction, Product 상태 동시에 변경
    @Transactional
    public Long updateTransactionStatus(Long transactionId, TransactionStatusUpdateDto request) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("거래 내역을 찾을 수 없습니다. ID: " + transactionId));

        Product product = transaction.getProduct();

        // 사용자 권한 확인
        if (!isAuthorizedToChangeStatus(transaction, request.getUserId())) {
            throw new SecurityException("권한x");
        }

        // 상태 변경
        transaction.updateStatus(request.getStatus());

        // Product 상태 동기화
        syncProductStatus(product, request.getStatus());

        return transaction.getId();
    }

    // Product 상태 동기화 메서드
    private void syncProductStatus(Product product, TransactionStatus newStatus) {
        if (newStatus == TransactionStatus.SOLD || newStatus == TransactionStatus.CANCELED) {
            // 거래가 완료되거나 취소되면, 상태는 판매중으로 돌아가야 함
            product.updateStatus(TransactionStatus.SELLING);

            if (newStatus == TransactionStatus.SOLD) {
                // 거래 완료 시 상품은 판매완료로 상태 변경
                product.updateStatus(TransactionStatus.SOLD);
            }

        } else {
            // 예약중 상태로 동기화
            product.updateStatus(newStatus);
        }
    }

    // 권한 확인
    private boolean isAuthorizedToChangeStatus(Transaction transaction, Long requestUserId) {
        // 판매자와 구매자만 상태를 변경 가능
        return transaction.getSeller().getId().equals(requestUserId) ||
                transaction.getCustomer().getId().equals(requestUserId);
    }
}
