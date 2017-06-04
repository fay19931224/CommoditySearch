import org.apache.lucene.document.*;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Created by fay19 on 2017/6/3.
 */
public class Indexer
{
    private String[] fileName;
    public boolean readAllDocumentFile(String documentDirectory)
    {
        File documentFile=new File(documentDirectory);
        if (!documentFile.exists()||!documentFile.canRead())
        {
            System.out.println("read error!");
            //System.exit(1);
        }
        if(documentFile.isDirectory())
        {
            fileName=documentFile.list();
            return true;
        }
        return false;
    }
    public void run()
    {
        if(!readAllDocumentFile("./document"))
        {
            System.out.println("read all document error");
        }
        try {
            Path path = Paths.get("./data");
            Directory directory= FSDirectory.open(path);
            Analyzer analyzer=new StandardAnalyzer();
            IndexWriterConfig iwConfig=new IndexWriterConfig(analyzer);
            iwConfig.setOpenMode(OpenMode.CREATE);
            IndexWriter writer=new IndexWriter(directory,iwConfig);
            indexDocs(writer);
            writer.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    void indexDocs(IndexWriter writer) throws IOException
    {

        for(int i = 0; i < fileName.length; i++)
        {
            System.out.println("Indexing file " + fileName[i]);
            Document document = new Document();
            FileInputStream is = new FileInputStream("./document/"+fileName[i]);
            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            BufferedReader reader=new BufferedReader(new InputStreamReader(is,"big5"));
            try
            {
                String shopName = reader.readLine();
                String commodityName = reader.readLine();
                String commodityPrice = reader.readLine();
                String patWay = reader.readLine();
                String commodityPictureUrl = reader.readLine();
                String commodityUrl = reader.readLine();
                String commodityContent = reader.readLine();
                String content;
                System.out.println(shopName);
                document.add(new TextField("shopName", shopName, TextField.Store.YES));
                document.add(new TextField("commodityName", commodityName, TextField.Store.YES));
                document.add(new NumericDocValuesField("commodityPrice", Long.parseLong(commodityPrice)));
                document.add(new TextField("patWay", patWay, TextField.Store.YES));
                document.add(new TextField("commodityPictureUrl", commodityPictureUrl, TextField.Store.YES));
                document.add(new TextField("commodityUrl", commodityUrl, TextField.Store.YES));
                document.add(new TextField("commodityContent", commodityContent, TextField.Store.YES));
                writer.addDocument(document);
            }
            catch (Exception ex)
            {
                System.out.print(ex.toString());
            }
            finally
            {
                is.close();
            }

        }
    }
}
