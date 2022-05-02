package org.prgms.voucherProgram.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.prgms.voucherProgram.customer.exception.CustomerIsNotExistsException;
import org.prgms.voucherProgram.customer.exception.DuplicateEmailException;
import org.prgms.voucherProgram.customer.repository.BlackListRepository;
import org.prgms.voucherProgram.customer.repository.CustomerRepository;
import org.prgms.voucherProgram.customer.domain.Customer;
import org.prgms.voucherProgram.customer.domain.Email;
import org.prgms.voucherProgram.customer.dto.CustomerRequest;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final String ERROR_VOUCHER_NOT_ASSIGN_MESSAGE = "해당 바우처를 가진 고객이 없습니다.";

    private final BlackListRepository blackListRepository;
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository, BlackListRepository fileCustomerRepository) {
        this.customerRepository = customerRepository;
        this.blackListRepository = fileCustomerRepository;
    }

    public Customer join(CustomerRequest customerRequest) {
        Customer customer = new Customer(UUID.randomUUID(), customerRequest.getName(), customerRequest.getEmail(),
            LocalDateTime.now());
        validateDuplicateCustomer(customer);
        return customerRepository.save(customer);
    }

    private void validateDuplicateCustomer(Customer customer) {
        customerRepository.findByEmail(customer.getEmail()).ifPresent(duplicateCustomer -> {
            throw new DuplicateEmailException();
        });
    }

    public Customer modify(Email email, CustomerRequest customerRequest) {
        Customer customer = findByEmail(email);
        customer.changeInformation(customerRequest.getName(), customerRequest.getEmail(), LocalDateTime.now());

        customerRepository.findByEmail(customer.getEmail())
            .ifPresent(findCustomer -> validateDuplicateEmail(customer, findCustomer));

        return customerRepository.update(customer);
    }

    private void validateDuplicateEmail(Customer customer, Customer findCustomer) {
        if (customer.isNotSameCustomer(findCustomer)) {
            throw new DuplicateEmailException();
        }
    }

    public Customer findByEmail(Email email) {
        return customerRepository.findByEmail(email.getEmail()).orElseThrow(() -> {
            throw new CustomerIsNotExistsException();
        });
    }

    public Customer findByVoucherId(UUID voucherId) {
        return customerRepository.findByVoucherId(voucherId)
            .orElseThrow(() -> {
                throw new CustomerIsNotExistsException(ERROR_VOUCHER_NOT_ASSIGN_MESSAGE);
            });
    }

    public List<Customer> findCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> findBlackList() {
        return blackListRepository.findBlackCustomers();
    }

    public void delete(Email email) {
        customerRepository.findByEmail(email.getEmail())
            .ifPresentOrElse(customer -> customerRepository.deleteByEmail(email.getEmail()), () -> {
                throw new CustomerIsNotExistsException();
            });
    }

    public Customer findById(UUID customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomerIsNotExistsException();
        });
    }
}