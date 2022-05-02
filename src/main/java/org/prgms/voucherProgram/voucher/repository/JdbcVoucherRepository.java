package org.prgms.voucherProgram.voucher.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.prgms.voucherProgram.global.utils.DatabaseUtils;
import org.prgms.voucherProgram.voucher.domain.Voucher;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"local", "default"})
public class JdbcVoucherRepository implements VoucherRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcVoucherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Voucher save(Voucher voucher) {
        int result = jdbcTemplate.update(
            "INSERT INTO voucher(voucher_id, customer_id, type ,discount, created_at) VALUES(UUID_TO_BIN(?), UUID_TO_BIN(?), ?, ?, ?)",
            DatabaseUtils.toBytes(voucher.getVoucherId()),
            DatabaseUtils.toBytes(voucher.getCustomerId()),
            voucher.getType(),
            voucher.getDiscountValue(),
            Timestamp.valueOf(voucher.getCreatedDateTime()));

        DatabaseUtils.validateExecute(result);
        return voucher;
    }

    @Override
    public Voucher update(Voucher voucher) {
        int result = jdbcTemplate.update(
            "UPDATE voucher SET type = ?, discount = ? WHERE voucher_id = UUID_TO_BIN(?)",
            voucher.getType(),
            voucher.getDiscountValue(),
            DatabaseUtils.toBytes(voucher.getVoucherId()));

        DatabaseUtils.validateExecute(result);
        return voucher;
    }

    @Override
    public List<Voucher> findAll(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jdbcTemplate.query("SELECT * FROM voucher WHERE created_at BETWEEN ?  AND  ?",
            DatabaseUtils.voucherRowMapper,
            startDateTime == null ? Timestamp.valueOf(LocalDateTime.MIN) : Timestamp.valueOf(startDateTime),
            endDateTime == null ? Timestamp.valueOf(LocalDateTime.now()) : Timestamp.valueOf(endDateTime));
    }

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        try {
            return Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT * FROM voucher WHERE voucher_id = UUID_TO_BIN(?)",
                    DatabaseUtils.voucherRowMapper, DatabaseUtils.toBytes(voucherId)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Voucher> findByType(int type) {
        return jdbcTemplate.query("SELECT * FROM voucher WHERE type = ?",
            DatabaseUtils.voucherRowMapper,
            type);
    }

    @Override
    public List<Voucher> findByTypeAndDate(int type, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jdbcTemplate.query("SELECT * FROM voucher WHERE type = ? AND created_at BETWEEN ?  AND  ?",
            DatabaseUtils.voucherRowMapper,
            type,
            startDateTime == null ? Timestamp.valueOf(LocalDateTime.MIN) : Timestamp.valueOf(startDateTime),
            endDateTime == null ? Timestamp.valueOf(LocalDateTime.now()) : Timestamp.valueOf(endDateTime));
    }

    @Override
    public void deleteById(UUID voucherId) {
        int result = jdbcTemplate.update("DELETE FROM voucher WHERE voucher_id = UUID_TO_BIN(?)",
            DatabaseUtils.toBytes(voucherId));
        DatabaseUtils.validateExecute(result);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM voucher");
    }

    @Override
    public Voucher assignCustomer(Voucher voucher) {
        int result = jdbcTemplate.update(
            "UPDATE voucher SET customer_id = UUID_TO_BIN(?) WHERE voucher_id = UUID_TO_BIN(?)",
            DatabaseUtils.toBytes(voucher.getCustomerId()),
            DatabaseUtils.toBytes(voucher.getVoucherId()));

        DatabaseUtils.validateExecute(result);
        return voucher;
    }

    @Override
    public List<Voucher> findByCustomerId(UUID customerId) {
        return jdbcTemplate.query("SELECT * FROM voucher WHERE customer_id = UUID_TO_BIN(?)",
            DatabaseUtils.voucherRowMapper,
            DatabaseUtils.toBytes(customerId));
    }

    @Override
    public List<Voucher> findByCustomerEmail(String customerEmail) {
        return jdbcTemplate.query(
            "SELECT * FROM voucher v join customer c on c.customer_id = v.customer_id where c.email = ?",
            DatabaseUtils.voucherRowMapper,
            customerEmail);
    }

    @Override
    public List<Voucher> findNotAssign() {
        return jdbcTemplate.query("SELECT * FROM voucher WHERE customer_id IS NULL",
            DatabaseUtils.voucherRowMapper);
    }

    @Override
    public List<Voucher> findAssigned() {
        return jdbcTemplate.query("SELECT * FROM voucher WHERE customer_id IS NOT NULL",
            DatabaseUtils.voucherRowMapper);
    }
}