package org.prgms.voucherProgram.service;

import java.util.List;
import java.util.UUID;

import org.prgms.voucherProgram.domain.customer.Customer;
import org.prgms.voucherProgram.domain.customer.Email;
import org.prgms.voucherProgram.domain.voucher.Voucher;
import org.prgms.voucherProgram.domain.voucher.VoucherType;
import org.prgms.voucherProgram.dto.VoucherRequest;
import org.prgms.voucherProgram.dto.WalletRequest;
import org.prgms.voucherProgram.exception.AlreadyAssignException;
import org.prgms.voucherProgram.exception.CustomerIsNotExistsException;
import org.prgms.voucherProgram.exception.NotFoundVoucherException;
import org.prgms.voucherProgram.exception.VoucherIsNotExistsException;
import org.prgms.voucherProgram.repository.customer.CustomerRepository;
import org.prgms.voucherProgram.repository.voucher.VoucherRepository;
import org.springframework.stereotype.Service;

@Service
public class VoucherService {

    public static final String ERROR_VOUCHER_IS_NOT_ASSIGN = "[ERROR] 해당 바우처는 아직 할당전 입니다.";

    private final VoucherRepository voucherRepository;
    private final CustomerRepository customerRepository;

    public VoucherService(VoucherRepository voucherRepository, CustomerRepository customerRepository) {
        this.voucherRepository = voucherRepository;
        this.customerRepository = customerRepository;
    }

    public Voucher create(VoucherRequest voucherRequest) {
        Voucher voucher = toEntity(UUID.randomUUID(), voucherRequest);
        return voucherRepository.save(voucher);
    }

    private Voucher toEntity(UUID voucherId, VoucherRequest voucherRequest) {
        return VoucherType.findByNumber(voucherRequest.getType())
            .constructor(voucherId, null, voucherRequest.getDiscountValue());
    }

    public Voucher modify(UUID voucherId, VoucherRequest voucherRequest) {
        findVoucher(voucherId);

        Voucher voucher = toEntity(voucherId, voucherRequest);
        return voucherRepository.update(voucher);
    }

    public Voucher findVoucher(UUID voucherId) {
        return voucherRepository.findById(voucherId).orElseThrow(() -> {
            throw new VoucherIsNotExistsException();
        });
    }

    public List<Voucher> findAllVoucher() {
        return voucherRepository.findAll();
    }

    public void delete(UUID voucherId) {
        voucherRepository.findById(voucherId)
            .ifPresentOrElse(voucher -> voucherRepository.deleteById(voucherId), () -> {
                throw new VoucherIsNotExistsException();
            });
    }

    public Voucher assignVoucher(WalletRequest walletRequest) {
        Voucher voucher = findVoucher(walletRequest.getVoucherId());
        validateAssign(voucher);
        Customer customer = findCustomer(walletRequest.getCustomerEmail());

        voucher.assignCustomer(customer.getCustomerId());
        return voucherRepository.assignCustomer(voucher);
    }

    public List<Voucher> findAssignVouchers(String customerEmail) {
        Customer customer = findCustomer(customerEmail);
        return voucherRepository.findByCustomerEmail(customer.getEmail());
    }

    public void deleteAssignVoucher(WalletRequest walletRequest) {
        Customer customer = findCustomer(walletRequest.getCustomerEmail());

        Voucher voucher = voucherRepository.findByCustomerId(customer.getCustomerId()).stream()
            .filter(findVoucher -> findVoucher.isSameVoucher(walletRequest.getVoucherId()))
            .findFirst()
            .orElseThrow(NotFoundVoucherException::new);

        voucherRepository.deleteById(voucher.getVoucherId());
    }

    public Customer findCustomer(UUID voucherId) {
        Voucher voucher = findVoucher(voucherId);
        validateNotAssign(voucher);

        return customerRepository.findByVoucherId(voucherId)
            .orElseThrow(CustomerIsNotExistsException::new);
    }

    private void validateNotAssign(Voucher voucher) {
        if (voucher.isNotAssign()) {
            throw new CustomerIsNotExistsException(ERROR_VOUCHER_IS_NOT_ASSIGN);
        }
    }

    private void validateAssign(Voucher voucher) {
        if (voucher.isAssign()) {
            throw new AlreadyAssignException();
        }
    }

    private Customer findCustomer(String requestEmail) {
        Email email = new Email(requestEmail);
        return customerRepository.findByEmail(email.getEmail()).orElseThrow(() -> {
            throw new CustomerIsNotExistsException();
        });
    }
}
