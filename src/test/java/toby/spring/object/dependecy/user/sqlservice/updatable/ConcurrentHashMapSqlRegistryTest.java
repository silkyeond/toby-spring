package toby.spring.object.dependecy.user.sqlservice.updatable;

class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
  @Override
  protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
    return new ConcurrentHashMapSqlRegistry();
  }
}
