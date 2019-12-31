////import com.google.common.collect.Lists;
////import com.google.gson.Gson;
////import org.apache.commons.lang.StringUtils;
//
//import java.io.*;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class JsonToBean {
//
//
//    private static void reader(String path) {
//        File f = new File(path);
//        try {
//            FileInputStream file = new FileInputStream(f);
//            InputStreamReader read = new InputStreamReader(file, "UTF-8");
//            BufferedReader br = new BufferedReader(read);
//            String line = br.readLine();
//            List<Map<String, Object>> objs = Lists.newLinkedList();
//            Gson gson = new Gson();
//            Map<String, Object> allKeys = new HashMap<String, Object>();
//            List<EntBean> beans = Lists.newLinkedList();
//            int lineNumber = 0;
//            while (StringUtils.isNotBlank(line)) {
//                EntBean obj = gson.fromJson(line, EntBean.class);
//                beans.add(obj);
////                Map<String, Object> obj = gson.fromJson(line, Map.class);
////                allKeys.putAll(obj);
//                line = br.readLine();
//                if (lineNumber > 500000) {
////                    System.out.println(allKeys.keySet());
//                    lineNumber = 0;
//                    beans.clear();
//                }
//                System.out.println(++lineNumber);
//            }
////            br.readLine()
////            while ()
////            String s = br.readLine();
////            Path
////            FileChannel open = FileChannel.open("", StandardOpenOption.READ);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        reader("F:\\dongguan_data\\rnt\\enterprise_data_gov\\enterprise_data_gov");
//    }
//
//    private MappedByteBuffer[] mappedByteBuffers;
//    private FileInputStream inputStream;
//    private FileChannel fileChannel;
//
//    private int bufferCountIndex = 0;
//    private int bufferCount;
//
//    private int byteBufferSize;
//    private byte[] byteBuffer;
//
//    public JsonToBean(String fileName, int byteBufferSize) throws IOException {
//        this.inputStream = new FileInputStream(fileName);
//        this.fileChannel = inputStream.getChannel();
//        long fileSize = fileChannel.size();
//        this.bufferCount = (int) Math.ceil((double) fileSize / (double) Integer.MAX_VALUE);
//        this.mappedByteBuffers = new MappedByteBuffer[bufferCount];
//        this.byteBufferSize = byteBufferSize;
//
//        long preLength = 0;
//        long regionSize = Integer.MAX_VALUE;
//        for (int i = 0; i < bufferCount; i++) {
//            if (fileSize - preLength < Integer.MAX_VALUE) {
//                regionSize = fileSize - preLength;
//            }
//            mappedByteBuffers[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
//            preLength += regionSize;
//        }
//    }
//
//    public synchronized int read() {
//        if (bufferCountIndex >= bufferCount) {
//            return -1;
//        }
//
//        int limit = mappedByteBuffers[bufferCountIndex].limit();
//        int position = mappedByteBuffers[bufferCountIndex].position();
//
//        int realSize = byteBufferSize;
//        if (limit - position < byteBufferSize) {
//            realSize = limit - position;
//        }
//        byteBuffer = new byte[realSize];
//        mappedByteBuffers[bufferCountIndex].get(byteBuffer);
//
//        //current fragment is end, goto next fragment start.
//        if (realSize < byteBufferSize && bufferCountIndex < bufferCount) {
//            bufferCountIndex++;
//        }
//        return realSize;
//    }
//
//    public void close() throws IOException {
//        fileChannel.close();
//        inputStream.close();
//        for (MappedByteBuffer byteBuffer : mappedByteBuffers) {
//            byteBuffer.clear();
//        }
//        byteBuffer = null;
//    }
//
//    public synchronized byte[] getCurrentBytes() {
//        return byteBuffer;
//    }
//}
