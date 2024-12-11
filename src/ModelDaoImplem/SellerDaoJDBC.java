package ModelDaoImplem;

import Entities.Department;
import Entities.Seller;
import Model.SellerDao;
import TestBD.Ressources.DB;
import TestBD.Ressources.DbException;
import com.mysql.cj.protocol.Resultset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;
    public SellerDaoJDBC(Connection connection) {
        this.conn = connection;
    }

    @Override
    public void insert(Seller obj) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {

            String checkQuery = "SELECT * FROM seller WHERE email = ?";
            st = conn.prepareStatement(checkQuery);
            st.setString(1, obj.getEmail());  // Verifica pelo email
            rs = st.executeQuery();

            if (rs.next()) {
                System.out.println("Seller with email " + obj.getEmail() + " already exists. Insertion skipped.");
                return;
            }


            String query = "INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES (?, ?, ?, ?, ?)";
            st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
            } else {
                throw new DbException("Insert failed");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }


    @Override
    public void update(Seller obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "UPDATE seller "
                            + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                            + "WHERE Id = ?");
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());
            st.setInt(6, obj.getId());

            st.executeUpdate();

        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);

        }

    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            String query = "DELETE FROM seller WHERE id = ?";
            st = conn.prepareStatement(query);
            st.setInt(1, id);  // Passa o ID para o SQL
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No seller found with ID " + id);
            } else {
                System.out.println("Seller with ID " + id + " deleted successfully.");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,Department.Name as DepName "
                    +
                    "FROM seller INNER JOIN Department "
                    +
                   " ON seller.DepartmentID = Department.Id "
                    +
                    " WHERE seller.Id = ? ");
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()){
                Department dep = instatiateDepartament(rs);
                Seller obj = instantiateSeller(rs, dep);
                return obj;
            }
            return null;
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller obj =new Seller();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setBaseSalary(rs.getDouble("BaseSalary"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setDepartment(dep);
        return obj;
    }

    private Department instatiateDepartament(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,Department.Name as DepName "
                            +
                            "FROM seller INNER JOIN department "
                            +
                            " ON seller.DepartmentID = department.Id ");
            rs = st.executeQuery();
            List<Seller> list = new ArrayList<>();
            Map<Integer,Department> map = new HashMap<>();

            while (rs.next()){
                Department dep = map.get(rs.getInt("DepartmentId"));
                if(dep == null){
                    dep = instatiateDepartament(rs);
                    map.put(rs.getInt("DepartmentId"),dep);
                }

                Seller obj = instantiateSeller(rs, dep);
                list.add (obj);
            }
            return list;
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,Department.Name as DepName "
                            +
                            "FROM seller INNER JOIN department "
                            +
                            " ON seller.DepartmentID = department.Id "
                            +
                            " WHERE Department.Id = ? ");
            st.setInt(1, department.getId());

            rs = st.executeQuery();
            List<Seller> list = new ArrayList<>();
            Map<Integer,Department> map = new HashMap<>();

            while (rs.next()){
                Department dep = map.get(rs.getInt("DepartmentId"));
                if(dep == null){
                    dep = instatiateDepartament(rs);
                    map.put(rs.getInt("DepartmentId"),dep);
                }

                Seller obj = instantiateSeller(rs, dep);
                list.add (obj);
            }
            return list;
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }
    }


