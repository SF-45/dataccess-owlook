package space.sadfox.dataccess.dataccess;

import space.sadfox.owlook.owlery.OwlAdapter;

public class ParserEntityAdapter extends OwlAdapter<ParserEntity> {

  @Override
  protected Class<ParserEntity> getTarget() {
    return ParserEntity.class;
  }


}
