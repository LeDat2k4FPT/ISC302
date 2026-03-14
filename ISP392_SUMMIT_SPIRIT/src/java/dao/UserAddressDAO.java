/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.UserAddressDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import utils.DBUtils;

/**
 *
 * @author gmt
 */
public class UserAddressDAO {

    public void insertAddressInfo(UserAddressDTO dto) throws Exception {
        String sql = "INSERT INTO UserAddressInfo (OrderID, Country, FullName, Phone, Email, Address, District, City) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getOrderID());
            ps.setString(2, dto.getCountry());
            ps.setString(3, dto.getFullName());
            ps.setString(4, dto.getPhone());
            ps.setString(5, dto.getEmail());
            ps.setString(6, dto.getAddress());
            ps.setString(7, dto.getDistrict());
            ps.setString(8, dto.getCity());
            ps.executeUpdate();
        }
    }
    
}
