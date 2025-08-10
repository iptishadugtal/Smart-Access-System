
import com.amdocs.sas.bean.Visitor;
import com.amdocs.sas.services.VisitorImpl;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VisitorImplTest {

    private static VisitorImpl visitorImpl;
    private static Visitor testVisitor;

    @BeforeAll
    static void setup() {
        visitorImpl = new VisitorImpl();
        testVisitor = new Visitor();
        testVisitor.setName("Test User");
        testVisitor.setEmail("testuser@example.com");
        testVisitor.setContact("9999999999");
        testVisitor.setPassword("test123");
    }

    @Test
    @Order(1)
    void testRegisterVisitor() {
        visitorImpl.registerVisitor(testVisitor);
        assertTrue(testVisitor.getVisitorId() > 0, "Visitor ID should be set after registration");
    }

    @Test
    @Order(2)
    void testLoginVisitor() {
        Visitor loggedIn = visitorImpl.loginVisitor(testVisitor.getVisitorId(), testVisitor.getPassword());
        assertNotNull(loggedIn, "Login failed – returned null");
        assertEquals(testVisitor.getEmail(), loggedIn.getEmail(), "Logged-in email mismatch");
    }
}
