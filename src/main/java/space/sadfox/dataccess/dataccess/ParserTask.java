package space.sadfox.dataccess.dataccess;

import java.util.List;
import java.util.Optional;
import javafx.concurrent.Task;
import space.sadfox.owlook.base.owl.Owl;

public abstract class ParserTask extends Task<List<DataEntity>> {
  private final Owl<ParserEntity> entityProvider;

  public ParserTask(Owl<ParserEntity> entityProvider) {
    this.entityProvider = entityProvider;
  }

  public Owl<ParserEntity> getParserEntity() {
    return entityProvider;
  }

  public Optional<ParserProvider> getParserProvider() {
    return getParserEntity().entity().getParserProviderSafe();
  }
}
