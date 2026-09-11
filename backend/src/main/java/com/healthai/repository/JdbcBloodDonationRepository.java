package com.healthai.repository;

import com.healthai.entity.BloodBank;
import com.healthai.entity.BloodDonor;
import com.healthai.entity.BloodRequest;
import com.healthai.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JDBC repository implementation for Blood Donation with thread-safe offline fallback dataset.
 */
@Repository
public class JdbcBloodDonationRepository implements BloodDonationRepository {

    private final List<BloodDonor> fallbackDonors = Collections.synchronizedList(new ArrayList<>());
    private final List<BloodRequest> fallbackRequests = Collections.synchronizedList(new ArrayList<>());
    private final List<BloodBank> fallbackBanks = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger donorIdGen = new AtomicInteger(100);
    private final AtomicInteger requestIdGen = new AtomicInteger(100);

    public JdbcBloodDonationRepository() {
        initFallbackData();
    }

    @Override
    public BloodDonor registerDonor(BloodDonor donor) {
        String sql = "INSERT INTO blood_donors (full_name, blood_group, age, gender, city, phone, email, last_donation_date, is_available, total_donations) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, donor.getFullName());
            ps.setString(2, donor.getBloodGroup());
            ps.setInt(3, donor.getAge());
            ps.setString(4, donor.getGender());
            ps.setString(5, donor.getCity());
            ps.setString(6, donor.getPhone());
            ps.setString(7, donor.getEmail());
            ps.setDate(8, donor.getLastDonationDate());
            ps.setBoolean(9, donor.isAvailable());
            ps.setInt(10, donor.getTotalDonations());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    donor.setId(rs.getInt(1));
                }
            }
            donor.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            fallbackDonors.add(0, donor);
            return donor;
        } catch (Exception e) {
            System.err.println("[JdbcBloodDonationRepository] MySQL insert failed; saving to in-memory store. Reason: " + e.getMessage());
            donor.setId(donorIdGen.incrementAndGet());
            donor.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            fallbackDonors.add(0, donor);
            return donor;
        }
    }

    @Override
    public List<BloodDonor> searchDonors(String bloodGroup, String city, Boolean onlyAvailable) {
        StringBuilder sql = new StringBuilder("SELECT id, full_name, blood_group, age, gender, city, phone, email, last_donation_date, is_available, total_donations, created_at FROM blood_donors WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (bloodGroup != null && !bloodGroup.trim().isEmpty() && !"All".equalsIgnoreCase(bloodGroup)) {
            sql.append("AND LOWER(blood_group) = LOWER(?) ");
            params.add(bloodGroup.trim());
        }
        if (city != null && !city.trim().isEmpty() && !"All".equalsIgnoreCase(city)) {
            sql.append("AND LOWER(city) LIKE LOWER(?) ");
            params.add("%" + city.trim() + "%");
        }
        if (onlyAvailable != null && onlyAvailable) {
            sql.append("AND is_available = TRUE ");
        }
        sql.append("ORDER BY id DESC");

        List<BloodDonor> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDonor(rs));
                }
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcBloodDonationRepository] MySQL search failed; filtering fallback. Reason: " + e.getMessage());
        }

        List<BloodDonor> filtered = new ArrayList<>();
        synchronized (fallbackDonors) {
            for (BloodDonor d : fallbackDonors) {
                boolean matchGroup = bloodGroup == null || bloodGroup.isEmpty() || "All".equalsIgnoreCase(bloodGroup) || d.getBloodGroup().equalsIgnoreCase(bloodGroup.trim());
                boolean matchCity = city == null || city.isEmpty() || "All".equalsIgnoreCase(city) || (d.getCity() != null && d.getCity().toLowerCase().contains(city.trim().toLowerCase()));
                boolean matchAvail = onlyAvailable == null || !onlyAvailable || d.isAvailable();

                if (matchGroup && matchCity && matchAvail) {
                    filtered.add(d);
                }
            }
        }
        return filtered;
    }

    @Override
    public List<BloodDonor> getAllDonors() {
        return searchDonors(null, null, null);
    }

    @Override
    public BloodRequest createRequest(BloodRequest request) {
        String sql = "INSERT INTO blood_requests (patient_name, blood_group, units_needed, hospital_name, city, contact_person, contact_phone, urgency_level, status, requirement_reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, request.getPatientName());
            ps.setString(2, request.getBloodGroup());
            ps.setInt(3, request.getUnitsNeeded());
            ps.setString(4, request.getHospitalName());
            ps.setString(5, request.getCity());
            ps.setString(6, request.getContactPerson());
            ps.setString(7, request.getContactPhone());
            ps.setString(8, request.getUrgencyLevel() == null ? "Urgent" : request.getUrgencyLevel());
            ps.setString(9, request.getStatus() == null ? "Open" : request.getStatus());
            ps.setString(10, request.getRequirementReason());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    request.setId(rs.getInt(1));
                }
            }
            request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            fallbackRequests.add(0, request);
            return request;
        } catch (Exception e) {
            System.err.println("[JdbcBloodDonationRepository] MySQL insert request failed; saving to fallback store. Reason: " + e.getMessage());
            request.setId(requestIdGen.incrementAndGet());
            request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            fallbackRequests.add(0, request);
            return request;
        }
    }

    @Override
    public List<BloodRequest> searchRequests(String bloodGroup, String city, String status) {
        StringBuilder sql = new StringBuilder("SELECT id, patient_name, blood_group, units_needed, hospital_name, city, contact_person, contact_phone, urgency_level, status, requirement_reason, created_at FROM blood_requests WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (bloodGroup != null && !bloodGroup.trim().isEmpty() && !"All".equalsIgnoreCase(bloodGroup)) {
            sql.append("AND LOWER(blood_group) = LOWER(?) ");
            params.add(bloodGroup.trim());
        }
        if (city != null && !city.trim().isEmpty() && !"All".equalsIgnoreCase(city)) {
            sql.append("AND LOWER(city) LIKE LOWER(?) ");
            params.add("%" + city.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty() && !"All".equalsIgnoreCase(status)) {
            sql.append("AND LOWER(status) = LOWER(?) ");
            params.add(status.trim());
        }
        sql.append("ORDER BY id DESC");

        List<BloodRequest> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRequest(rs));
                }
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcBloodDonationRepository] MySQL search requests failed; filtering fallback. Reason: " + e.getMessage());
        }

        List<BloodRequest> filtered = new ArrayList<>();
        synchronized (fallbackRequests) {
            for (BloodRequest r : fallbackRequests) {
                boolean matchGroup = bloodGroup == null || bloodGroup.isEmpty() || "All".equalsIgnoreCase(bloodGroup) || r.getBloodGroup().equalsIgnoreCase(bloodGroup.trim());
                boolean matchCity = city == null || city.isEmpty() || "All".equalsIgnoreCase(city) || (r.getCity() != null && r.getCity().toLowerCase().contains(city.trim().toLowerCase()));
                boolean matchStatus = status == null || status.isEmpty() || "All".equalsIgnoreCase(status) || (r.getStatus() != null && r.getStatus().equalsIgnoreCase(status.trim()));

                if (matchGroup && matchCity && matchStatus) {
                    filtered.add(r);
                }
            }
        }
        return filtered;
    }

    @Override
    public List<BloodRequest> getAllRequests() {
        return searchRequests(null, null, null);
    }

    @Override
    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE blood_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                updateFallbackStatus(requestId, status);
                return true;
            }
        } catch (Exception e) {
            System.err.println("[JdbcBloodDonationRepository] MySQL update request status failed: " + e.getMessage());
        }

        return updateFallbackStatus(requestId, status);
    }

    private boolean updateFallbackStatus(int requestId, String status) {
        synchronized (fallbackRequests) {
            for (BloodRequest r : fallbackRequests) {
                if (r.getId() == requestId) {
                    r.setStatus(status);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<BloodBank> getBloodBanks(String city) {
        StringBuilder sql = new StringBuilder("SELECT id, bank_name, city, address, phone, operating_hours, verified, created_at FROM blood_banks WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (city != null && !city.trim().isEmpty() && !"All".equalsIgnoreCase(city)) {
            sql.append("AND LOWER(city) LIKE LOWER(?) ");
            params.add("%" + city.trim() + "%");
        }
        sql.append("ORDER BY id ASC");

        List<BloodBank> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBank(rs));
                }
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcBloodDonationRepository] MySQL banks query failed; using fallback. Reason: " + e.getMessage());
        }

        List<BloodBank> filtered = new ArrayList<>();
        synchronized (fallbackBanks) {
            for (BloodBank b : fallbackBanks) {
                if (city == null || city.isEmpty() || "All".equalsIgnoreCase(city) || (b.getCity() != null && b.getCity().toLowerCase().contains(city.trim().toLowerCase()))) {
                    filtered.add(b);
                }
            }
        }
        return filtered;
    }

    private BloodDonor mapResultSetToDonor(ResultSet rs) throws Exception {
        return new BloodDonor(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("blood_group"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getString("city"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getDate("last_donation_date"),
                rs.getBoolean("is_available"),
                rs.getInt("total_donations"),
                rs.getTimestamp("created_at")
        );
    }

    private BloodRequest mapResultSetToRequest(ResultSet rs) throws Exception {
        return new BloodRequest(
                rs.getInt("id"),
                rs.getString("patient_name"),
                rs.getString("blood_group"),
                rs.getInt("units_needed"),
                rs.getString("hospital_name"),
                rs.getString("city"),
                rs.getString("contact_person"),
                rs.getString("contact_phone"),
                rs.getString("urgency_level"),
                rs.getString("status"),
                rs.getString("requirement_reason"),
                rs.getTimestamp("created_at")
        );
    }

    private BloodBank mapResultSetToBank(ResultSet rs) throws Exception {
        return new BloodBank(
                rs.getInt("id"),
                rs.getString("bank_name"),
                rs.getString("city"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("operating_hours"),
                rs.getBoolean("verified"),
                rs.getTimestamp("created_at")
        );
    }

    private void initFallbackData() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Initial Donors
        fallbackDonors.add(new BloodDonor(1, "Rohit Sharma", "O+", 29, "Male", "Indore", "+91 98260 11223", "rohit.sharma@example.com", Date.valueOf("2025-11-10"), true, 4, now));
        fallbackDonors.add(new BloodDonor(2, "Ananya Verma", "A+", 24, "Female", "Bhopal", "+91 98930 44556", "ananya.verma@example.com", Date.valueOf("2025-12-05"), true, 2, now));
        fallbackDonors.add(new BloodDonor(3, "Vikramaditya Joshi", "B+", 32, "Male", "Dewas", "+91 94250 77889", "vikram.joshi@example.com", Date.valueOf("2025-10-18"), true, 6, now));
        fallbackDonors.add(new BloodDonor(4, "Pooja Nair", "O-", 27, "Female", "Indore", "+91 97550 22334", "pooja.nair@example.com", Date.valueOf("2025-08-20"), true, 3, now));
        fallbackDonors.add(new BloodDonor(5, "Sameer Khan", "AB+", 35, "Male", "Ujjain", "+91 98270 99881", "sameer.khan@example.com", Date.valueOf("2025-12-28"), true, 5, now));
        fallbackDonors.add(new BloodDonor(6, "Neha Kulkarni", "A-", 26, "Female", "Indore", "+91 98931 66778", "neha.kulkarni@example.com", Date.valueOf("2025-09-15"), true, 1, now));
        fallbackDonors.add(new BloodDonor(7, "Aman Gupta", "B-", 30, "Male", "Bhopal", "+91 94066 33445", "aman.gupta@example.com", Date.valueOf("2025-11-01"), true, 3, now));
        fallbackDonors.add(new BloodDonor(8, "Priya Patidar", "AB-", 28, "Female", "Dewas", "+91 97520 88990", "priya.patidar@example.com", Date.valueOf("2025-07-14"), true, 2, now));

        // Initial Requests
        fallbackRequests.add(new BloodRequest(1, "Sunita Devi", "O-", 2, "MY Hospital, Indore", "Indore", "Dr. Alok Mishra", "+91 98261 00112", "Critical", "Open", "Emergency caesarean section with acute maternal postpartum hemorrhage.", now));
        fallbackRequests.add(new BloodRequest(2, "Deepak Malviya", "B+", 3, "Bhopal Memorial Hospital", "Bhopal", "Rajesh Malviya (Brother)", "+91 98932 11223", "Urgent", "Open", "Undergoing cardiac bypass graft surgery scheduled tomorrow.", now));
        fallbackRequests.add(new BloodRequest(3, "Kavita Rathore", "A+", 1, "District Civil Hospital, Dewas", "Dewas", "Suresh Rathore (Husband)", "+91 94251 44556", "Critical", "Open", "Emergency road traffic trauma and splenic laceration blood loss.", now));
        fallbackRequests.add(new BloodRequest(4, "Master Aarav", "AB+", 2, "CHL Hospitals, Indore", "Indore", "Meena Jain (Mother)", "+91 97551 77889", "Standard", "In Progress", "Scheduled elective pediatric orthopedic bone surgery.", now));

        // Initial Banks
        fallbackBanks.add(new BloodBank(1, "Indore Red Cross Regional Blood Centre", "Indore", "Near Collectorate Office, Moti Tabela, Indore, MP", "+91 731 2544108", "24/7 Open", true, now));
        fallbackBanks.add(new BloodBank(2, "MY Hospital Model Blood Bank", "Indore", "Agra Bombay Rd, Sanyogitaganj, Indore, MP", "+91 731 2527200", "24/7 Open", true, now));
        fallbackBanks.add(new BloodBank(3, "Bhopal Red Cross Society Blood Bank", "Bhopal", "Red Cross Bhawan, Shivaji Nagar, Bhopal, MP", "+91 755 2551108", "24/7 Open", true, now));
        fallbackBanks.add(new BloodBank(4, "Gandhi Medical College Blood Bank", "Bhopal", "Sultania Rd, Royal Market, Bhopal, MP", "+91 755 2540590", "24/7 Open", true, now));
        fallbackBanks.add(new BloodBank(5, "District Hospital Blood Centre Dewas", "Dewas", "Civil Hospital Campus, AB Road, Dewas, MP", "+91 7272 252108", "24/7 Open", true, now));
    }
}
