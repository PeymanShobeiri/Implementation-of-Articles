package DAG;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DagUtils {
    public static Adag readWorkflowDescription(String wfdescFile) throws Throwable {
        try {
            IBindingFactory bfact = BindingDirectory.getFactory(Adag.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            FileInputStream in;
            in = new FileInputStream(ClassLoader.getSystemResource(wfdescFile).getFile());
            return (Adag) uctx.unmarshalDocument(in, null);
        } catch (JiBXException e1) {
            System.out.println("JIBX exception " + e1);
            throw e1;
        } catch (FileNotFoundException e2) {
            System.out.println("File not found! " + e2);
            throw e2;
        }
    }
}
