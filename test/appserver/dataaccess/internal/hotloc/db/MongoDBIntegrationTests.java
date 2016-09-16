package dk.aau.astep.appserver.dataaccess.internal.hotloc.db;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.exception.BusinessException;
import org.bson.Document;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static dk.aau.astep.appserver.dataaccess.internal.hotloc.db.Statics.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class MongoDBIntegrationTests {

  @BeforeClass
  public static void beforeAll(){
    mongoClient = new Mongodb("astepmongodb", "test");
    database    = mongoClient.getDatabase();
    database.createCollection("test");
    collection  = database.getCollection("test");
    Document document = new Document()
                          .append("shardCollection","astepmongodb.test")
                          .append("key",new Document("_id", "hashed"));
    mongoClient.getConnection().getDatabase("admin").runCommand(document);
    mongoClient.saveLocation(userLocationSpy);
    mongoClient.saveLocation(friendLocationSpy);
    mongoClient.saveLocation(friendOldLocationSpy);
    mongoClient.saveLocation(motherLocationSpy);
    mongoClient.saveLocation(russianLocationSpy);
    mongoClient.saveLocation(userOldLocationSpy);
    mongoClient.saveLocation(russianOldLocationSpy);
    mongoClient.saveLocation(outsideNowLocationSpy);
    mongoClient.saveLocation(insideOldLocationSpy);
    mongoClient.saveLocation(outsideOldLocationSpy);
    mongoClient.saveLocation(insideNowLocationSpy);
    collection.createIndex(new Document("location", "2dsphere"));
    collection.createIndex(new Document("timestamp", -1));
    ArrayList points = new ArrayList();
    points.add(RectCoordinateMaxSpy);
    points.add(PolygonCoordinateMinSpy);
    points.add(RectCoordinateMinSpy);
    points.add(PolygonCoordinateMaxSpy);
    polygonSpy = new Polygon(points);
  }

  @AfterClass
  public static void afterAll(){ database.getCollection("test").drop(); }

  @Before
  public void setup() {
  }

  @Test
  public void sanityTest() {
    assertThat(1,equalTo(1));
  }

  @Test
  public void connectTest() {
    Assert.assertNotNull(mongoClient.getConnection());
  }

  @Test
  public void reconnectTest() {
    Assert.assertNotNull(mongoClient.reconnect());
  }

  @Test(expected = BusinessException.class)
  public void connectToInvalidDatabaseTest() {
    new Mongodb("null","null");
  }

  @Test
  public void collectionTest() {
    assertThat(mongoClient.getCollection().getNamespace().getCollectionName(), equalTo("test"));
  }

  @Test
  public void saveLocation() {
    Assert.assertTrue(mongoClient.saveLocation(userLocationSpy));
  }

  @Test(expected = BusinessException.class)
  public void saveLocationIllegal() {
    Location legal = spy(new Location(new Coordinate(999,-999), Instant.MAX,"Drop table", new Precision(-2, -1)));
    Assert.assertFalse(mongoClient.saveLocation(legal));
  }

  @Test
  public void saveLocationLegal() {
    Location legal = spy(new Location(new Coordinate(0,0),Instant.EPOCH,"", precisionSpy));
    Assert.assertTrue(mongoClient.saveLocation(legal));
  }

  @Test
  public void saveLocationBulk() {
    Location legal1 = spy(new Location(new Coordinate(0,0),Instant.EPOCH,"", precisionSpy));
    Location legal2 = spy(new Location(new Coordinate(0,0),Instant.EPOCH.minus(Duration.ofNanos(1)),"",
            precisionSpy));
    ArrayList list = new ArrayList();
    list.add(legal1);
    list.add(legal2);
    Assert.assertTrue(mongoClient.saveLocations(list));
  }

  @Test(expected = IllegalArgumentException.class)
  public void saveEmptyLocationBulk() {
    ArrayList list = new ArrayList();
    Assert.assertFalse(mongoClient.saveLocations(list));
  }

  @Test(expected = BusinessException.class)
  public void saveLocationBulkIllegal() {
    Location illegal1 = spy(new Location(new Coordinate(0,-999),Instant.now(),"", precisionSpy));
    Location illegal2 = spy(new Location(new Coordinate(0,999),Instant.EPOCH.minus(Duration.ofNanos(1)),"",
            precisionSpy));
    ArrayList list = new ArrayList();
    list.add(illegal1);
    list.add(illegal2);
    Assert.assertFalse(mongoClient.saveLocations(list));
  }

  @Test
  public void getAllLocationSingleUser() {
    List<Location> list = mongoClient.getAllLocations(userId);
    assertThat(list, hasItem(equalTo(userLocationSpy)));
    assertThat(list, hasItem(equalTo(userOldLocationSpy)));
    assertThat(list, not(hasItem(equalTo(friendLocationSpy))));
  }

  @Test
  public void getNewestLocationSingleUser() {
    ArrayList<String> userIds = new ArrayList();
    userIds.add(userId);
    List<Location> list = mongoClient.getNewestLocation(userIds);
    Location single = mongoClient.getNewestLocation(userId);
    assertThat(list, hasItem(equalTo(userLocationSpy)));
    assertThat(single, equalTo(userLocationSpy));
    assertThat(list, hasItem(equalTo(single)));
  }

  @Test
  public void getNewestLocationSingleUserEmptyFriends() {
    ArrayList<String> userIds = new ArrayList();
    List<Location> list = mongoClient.getNewestLocation(userIds);
    assertTrue(list.isEmpty());
  }

  @Test
  public void getNewestLocationMultipleUsers() {
    ArrayList<String> userIds = new ArrayList();
    userIds.add(userId);
    userIds.add(friendId);
    List<Location> list = mongoClient.getNewestLocation(userIds);
    assertThat(list, hasItems(equalTo(userLocationSpy),equalTo(friendLocationSpy)));
    assertThat(list, not(hasItems(equalTo(userOldLocationSpy),equalTo(friendOldLocationSpy))));
  }

  @Test
  public void getNewestLocationsFromTime() {
    ArrayList list = new ArrayList();
    list.add(userId);
    List<Location> locations = mongoClient.getNewestLocationsFromTime(list, instantBefore);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItem(equalTo(userLocationSpy)));
  }

  @Test
  public void getNewestLocationsFromTimeFuture() {
    ArrayList list = new ArrayList();
    list.add(userId);
    List<Location> locations = mongoClient.getNewestLocationsFromTime(list, instantAfter);
    assertThat(locations, not(hasItem(equalTo(userLocationSpy))));
    assertTrue(locations.isEmpty());
  }

  @Test
  public void getNewestLocationsInAreaPolygon() {
    ArrayList list = new ArrayList();
    list.add(userId);
    List<Location> locations = mongoClient.getNewestLocationsInArea(polygonSpy);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItem(equalTo(userLocationSpy)));
  }

  @Test
  public void getNewestLocationsInAreaRectangle() {
    ArrayList list = new ArrayList();
    list.add(userId);
    list.add(russianId);
    list.add(rectTestId1);
    list.add(rectTestId2);
    List<Location> locations = mongoClient.getNewestLocationsInArea(rectSpy,list);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItems(equalTo(userLocationSpy),equalTo(insideNowLocationSpy)));
    assertThat(locations, not(hasItems(equalTo(russianLocationSpy),equalTo(insideOldLocationSpy),equalTo(outsideOldLocationSpy),equalTo(outsideNowLocationSpy))));
  }

  @Test
  public void getNewestLocationsOutsideArea() {
    ArrayList list = new ArrayList();
    list.add(userId);
    list.add(russianId);
    list.add(rectTestId1);
    list.add(rectTestId2);
    List<Location> locations = mongoClient.getNewestLocationsOutsideArea(rectSpy,list);
    assertThat(locations, not(hasItems(equalTo(userLocationSpy),equalTo(insideOldLocationSpy),equalTo(outsideOldLocationSpy),equalTo(insideNowLocationSpy))));
    assertThat(locations, hasItems(equalTo(russianLocationSpy),equalTo(outsideNowLocationSpy)));
  }

  @Test
  public void getLocationsAtTimeInterval() {
    List<Location> locations = mongoClient.getLocationsAtTimeInterval(userId, instantBefore, instantAfter);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItem(equalTo(userLocationSpy)));
  }

  @Test
  public void getLocationsInAreaAtTimeIntervalRectangle() {
    List<Location> locations = mongoClient.getLocationsInAreaAtTimeInterval(rectSpy, instantBefore, instantAfter,
            userId);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItem(equalTo(userLocationSpy)));
  }

  @Test
  public void getLocationsInAreaAtTimeIntervalPolygon() {
    List<Location> locations = mongoClient.getLocationsInAreaAtTimeInterval(polygonSpy, instantBefore, instantAfter);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItems(equalTo(userLocationSpy),equalTo(friendLocationSpy)));
    assertThat(locations, not(hasItems(equalTo(russianLocationSpy))));
  }

  @Test
  public void getAllLocationsInRadius() {
    List<Location> locations = mongoClient.getAllLocationsInRadius(userCoordinateSpy, 50d);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItems(equalTo(userLocationSpy),equalTo(friendLocationSpy)));
    assertThat(locations, not(hasItems(equalTo(motherLocationSpy),equalTo(russianLocationSpy))));
  }

  @Test
  public void getAllLocationsInRadiusMultipleUsers() {
    ArrayList list = new ArrayList();
    list.add(userId);
    list.add(friendId);
    list.add(motherId);
    List<Location> locations = mongoClient.getAllLocationsInRadius(userCoordinateSpy, 50d, list);
    Assert.assertNotNull(locations);
    assertThat(locations, hasItems(equalTo(userLocationSpy),equalTo(friendLocationSpy)));
    assertThat(locations, not(hasItems(equalTo(motherLocationSpy),equalTo(russianLocationSpy))));
  }



  @Test
  public void getNewestLocationsOutsideRadius() {
    ArrayList list = new ArrayList();
    list.add(userId);
    list.add(motherId);
    List<Location> locations = mongoClient.getNewestLocationsOutsideRadius(userCoordinateSpy, 50d, list);
    Assert.assertNotNull(locations);
    assertThat(locations, not(hasItems(equalTo(userLocationSpy),equalTo(russianLocationSpy),equalTo(friendLocationSpy))));
    assertThat(locations, hasItem(equalTo(motherLocationSpy)));
  }


  @Test
  public void getNewestLocationsInRadiusTimeInterval() {
    List<Location> locations = mongoClient.getAllLocationsInRadius(userCoordinateSpy, 50d, instantBefore, instantAfter);
    Assert.assertNotNull(locations);
    assertThat(locations, not(hasItems(equalTo(userOldLocationSpy),equalTo(russianLocationSpy),equalTo(russianOldLocationSpy),equalTo(motherLocationSpy))));
    assertThat(locations, hasItems(equalTo(friendLocationSpy),equalTo(userLocationSpy)));
  }
}