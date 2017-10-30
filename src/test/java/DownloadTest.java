import com.drewhoener.artifact.MavenArtifact;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertTrue;

public class DownloadTest {

    @Test(expected = IOException.class)
    public void testArtifactDownload() throws Exception {

        MavenArtifact artifact = new MavenArtifact("me.drewhoener", "GameCore", "1.4-SNAPSHOT", "jar");
        HttpURLConnection connection = artifact.getCompleteConnection("https://drewhoener.com/repo/repository/public/", "admin", "admin123");
        assertTrue(connection != null);
    }

}
