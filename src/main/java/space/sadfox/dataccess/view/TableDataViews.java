package space.sadfox.dataccess.view;

import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.utils.Owlook;

public class TableDataViews {
  public static Owl<TableDataView> createTableDataView() {
    try {
      return OwlLoader.INSTANCE.createOwl(TableDataView.class);
    } catch (Exception e) {
      Owlook.registerException(1, e);
      return null;
    }
  }

  public static boolean deleteTableDataView(Owl<TableDataView> viewOwl) {
    try {
      OwlLoader.INSTANCE.deleteOwl(viewOwl);
      return true;
    } catch (Exception e) {
      Owlook.registerException(1, e);
      return false;
    }
  }
}
