import com.drewhoener.artifact.MavenArtifact;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;

public class DownloadTest {

    @Test
    public void testArtifactDownload() throws Exception {

        MavenArtifact artifact = new MavenArtifact("me.drewhoener", "GameCore", "1.4-SNAPSHOT", "jar");
        HttpURLConnection connection = artifact.getCompleteConnection("https://drewhoener.com/repo/repository/public/", "admin", "admin123");


    }

}
