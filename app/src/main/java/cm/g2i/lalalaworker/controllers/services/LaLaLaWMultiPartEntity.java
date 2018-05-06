package cm.g2i.lalalaworker.controllers.services;

/**
 * Created by Sim'S on 14/08/2017.
 */

/*import android.app.ProgressDialog;
import android.os.RecoverySystem;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;*/

/*@SuppressWarnings("deprecation")
public class LaLaLaWMultiPartEntity extends MultipartEntity {

    public static interface ProgressListener{
        public void transferred(long num);
    }
    private ProgressListener progressListener;

    public LaLaLaWMultiPartEntity(ProgressListener progressListener){
        super();
        this.progressListener = progressListener;
    }

    public LaLaLaWMultiPartEntity(final HttpMultipartMode mode, ProgressListener progressListener){
        super(mode);
        this.progressListener = progressListener;
    }

    public LaLaLaWMultiPartEntity(final HttpMultipartMode mode, final String boundary, final Charset charset,
                                  ProgressListener progressListener){
        super(mode, boundary, charset);
        this.progressListener = progressListener;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        super.writeTo(new CountingOutputStream(outstream, this.progressListener));
    }

    public static class CountingOutputStream extends FilterOutputStream{

        private ProgressListener progressListener;
        private long transferred;

        public CountingOutputStream(OutputStream out) {
            super(out);
        }

        public CountingOutputStream(OutputStream out, ProgressListener progressListener){
            super(out);
            this.transferred = 0;
            this.progressListener = progressListener;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
            this.progressListener.transferred(this.transferred);
        }

        public void write(int b) throws IOException {
            out.write(b);
            this.transferred++;
            this.progressListener.transferred(this.transferred);
        }
    }
}*/
