package Model;

import ModelDaoImplem.SellerDaoJDBC;
import TestBD.Ressources.DB;

public class DaoFactory {
    public static SellerDao createSellerDao(){
        return new SellerDaoJDBC(DB.getConnection());

    }
}
