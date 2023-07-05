package ClientUtils.Requester.fileupload;

import ClientUtils.Constants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;

public class FileUploadImpl implements FileUpload{

    private final static String UPLOAD_FILE_URL = Constants.BASE_URL + Constants.UPLOAD_FILE_URL;
    @Override
    public Request fileUploadRequest(File file) {
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("application/xml")))
                .addFormDataPart("file_path", file.getPath())
                .build();
        return new Request.Builder()
                .url(UPLOAD_FILE_URL)
                .post(body)
                .build();
    }
}
