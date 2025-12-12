package by.zgirskaya.course.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class AbstractModel implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  private UUID id;

  public UUID getId() { return this.id; }
  public void setId(UUID id) { this.id = id; }
}
