package org.prgms.voucherProgram.domain.wallet.controller;

import java.util.List;
import java.util.UUID;

import org.prgms.voucherProgram.domain.customer.domain.Customer;
import org.prgms.voucherProgram.domain.customer.domain.Email;
import org.prgms.voucherProgram.domain.customer.dto.CustomerDto;
import org.prgms.voucherProgram.domain.customer.service.CustomerService;
import org.prgms.voucherProgram.domain.voucher.dto.VoucherDto;
import org.prgms.voucherProgram.domain.voucher.service.VoucherService;
import org.prgms.voucherProgram.domain.wallet.dto.WalletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final VoucherService voucherService;
    private final CustomerService customerService;

    public WalletController(VoucherService voucherService,
        CustomerService customerService) {
        this.voucherService = voucherService;
        this.customerService = customerService;
    }

    @GetMapping
    public String walletPage() {
        return "wallet/wallet";
    }

    @GetMapping("vouchers/{email}/assign")
    public String walletAssignPage(@PathVariable("email") Email email, Model model) {
        List<VoucherDto> notAssignVouchers = voucherService.findNotAssignVouchers().stream()
            .map(VoucherDto::from)
            .toList();
        model.addAttribute("vouchers", notAssignVouchers);
        model.addAttribute("email", email);
        return "wallet/assign";
    }

    @PostMapping("vouchers/assign")
    public String assignVoucher(WalletRequest walletRequest) {
        voucherService.assignVoucher(walletRequest);
        return "redirect:/wallet/vouchers/" + walletRequest.getCustomerEmail();
    }

    @GetMapping("/vouchers")
    public String walletVouchersPage(Model model) {
        List<CustomerDto> customers = customerService.findCustomers().stream()
            .map(CustomerDto::from)
            .toList();
        model.addAttribute("customers", customers);
        return "wallet/vouchers";
    }

    @GetMapping("/vouchers/{email}")
    public String customerWalletPage(@PathVariable("email") Email email, Model model) {
        List<VoucherDto> vouchers = voucherService.findAssignVouchers(email).stream()
            .map(VoucherDto::from)
            .toList();
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("email", email);
        return "wallet/vouchers-show";
    }

    @GetMapping("/vouchers/{email}/delete/{voucherId}")
    public String deleteAssignVoucher(@PathVariable("email") Email email, @PathVariable("voucherId") UUID voucherId) {
        voucherService.delete(voucherId);
        return "redirect:/wallet/vouchers/" + email.getEmail();
    }

    @GetMapping("/customer")
    public String findCustomerPage(Model model) {
        List<VoucherDto> assignedVouchers = voucherService.findAssginedVouchers().stream()
            .map(VoucherDto::from)
            .toList();
        model.addAttribute("vouchers", assignedVouchers);
        return "wallet/customer";
    }

    @GetMapping("/customer/{voucherId}")
    public String findCustomer(@PathVariable("voucherId") UUID voucherId, Model model) {
        Customer customer = voucherService.findCustomer(voucherId);
        model.addAttribute("customer", customer);
        return "wallet/customer-show";
    }
}
