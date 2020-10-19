package gr.tvarsa.hrace.utility;

import gr.tvarsa.hrace.app.HorjeRace;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;

public class UtImage
{

    private static File toFile(Object fileObject)
    {
        if (fileObject == null) return null;
        try
        {
            if (fileObject instanceof String) return new File((String)fileObject);
            if (fileObject instanceof File) return ((File)fileObject).getCanonicalFile();
        }
        catch (Exception e)
        {}
        return null;
    }

    public static Image loadImage(Object jarObjectSource, Object imageObject, boolean fromDirectoryIfMissing)
    {
        File file = toFile(imageObject);
        if (file == null || jarObjectSource == null) return null;
        Image image = null;
        URL url = jarObjectSource.getClass().getResource(file.getPath());
        if (url != null)
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            try
            {
                MediaTracker tracker = new MediaTracker(new Container());
                image = toolkit.getImage(url);
                tracker.addImage(image, 0);
                tracker.waitForID(0);
            }
            catch (Exception e)
            {
                image = null;
            }
        }
        if (image == null & fromDirectoryIfMissing) image = loadImage(file);
        return image;
    }

    /**
     * fileObject is either file or string. Will load from JAR file relative path if content can be found there.
     */
    public static Image loadImage(Object fileObject)
    {
        File file = toFile(fileObject);
        Image image = null;
        if (file == null || !file.exists())
        {
            try
            {
                String path = "/" + fileObject.toString();
                while (path.contains("\\"))
                    path = path.replace('\\', '/');
                URL resource = HorjeRace.class.getResource(path);
                image = ImageIO.read(resource);
                return image;
            }
            catch (Exception e)
            {
                return null;
            }
        }
        try
        {
            image = ImageIO.read(file);
            // String path = Files.getCanonicalPath(file);
            // image = Toolkit.getDefaultToolkit().getImage(path);
            // MediaTracker mediaTracker = new MediaTracker(new Container());
            // mediaTracker.addImage(image, 0);
            // mediaTracker.waitForAll();
        }
        catch (Exception e)
        {
            image = null;
        }
        return image;
    }

}
