package Application;

import Entities.Department;
import Entities.Seller;
import Model.DaoFactory;
import Model.SellerDao;
import ModelDaoImplem.SellerDaoJDBC;
import TestBD.Ressources.DB;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class Program {
    public static void main(String[] args) {
        System.out.println("\n ==== TEST 1: FindById ====");
       SellerDao sellerDao = DaoFactory.createSellerDao();
        Seller seller = sellerDao.findById(2);
        System.out.println(seller);

        System.out.println("\n ==== TEST2: FindByDepartamentId ====");

        Department department = new Department(2, null);
        List <Seller> list = sellerDao.findByDepartment(department);
        for (Seller obj : list) {
            System.out.println(obj);

        }

        System.out.println("\n ==== TEST3: FindAll ====");
        list = sellerDao.findAll();
        for (Seller obj : list) {
            System.out.println(obj);

        }
        System.out.println("\n ==== TEST4: seller insert ====");
        Seller newSeller = new Seller(null, "Greg", "Grego@gmail.com", new Date(), 2.234, department);
        sellerDao.insert(newSeller);
        System.out.println("Inserted! new id -> " + newSeller.getId());

        System.out.println("\n ==== TEST5: seller update ====");
        seller = sellerDao.findById(1);
        seller.setName("Martha Waine");
        sellerDao.update(seller);
        System.out.println("Updated! new name -> ");

        System.out.println("\n ==== TEST6: seller delete ====");
        sellerDao.deleteById(20);
        System.out.println("Deleted sellers with ids 17, 18, 19.");
    }
}
