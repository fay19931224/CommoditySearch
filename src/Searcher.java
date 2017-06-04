import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.print.Doc;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
/**
 * Created by fay19 on 2017/6/4.
 */
public class Searcher
{
    public void search() throws Exception
    {
        Path path = Paths.get("./data");
        String field="shopName";
        IndexReader reader= DirectoryReader.open(FSDirectory.open(path));

        IndexSearcher searcher=new IndexSearcher(reader);
        Analyzer analyzer=new  StandardAnalyzer();
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
        QueryParser parser=new QueryParser(field,analyzer);

        while (true) {
            System.out.println("Enter query:");
            String line = in.readLine();
            if (line==null||line.length()==-1) {
                break;
            }
            line =line.trim();
            if (line.length()==0) {
                break;
            }
            Query query=parser.parse(line);
            System.out.println("searching for:"+query.toString(field));
            doPagingSearch(in, searcher, query, 10);
        }
        reader.close();
    }

    public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage) throws IOException
    {
        TopDocs resultsDocs=searcher.search(query, 5*hitsPerPage);
        ScoreDoc[] hits=resultsDocs.scoreDocs;
        int numTotalHits=resultsDocs.totalHits;
        System.out.println(numTotalHits+" total matchings!");
        int start=0;
        int end=Math.min(numTotalHits, hitsPerPage);
        while (true) {
            if (end>hits.length) {
                System.out.println("Only results 1 - " + hits.length + " of "
                                + numTotalHits
                                + " total matching documents collected.");
                System.out.println("Collect more (y/n) ?");
                String line = in.readLine();
                if (line.length() == 0 || line.charAt(0) == 'n') {
                    break;
                }
                hits = searcher.search(query, numTotalHits).scoreDocs;
            }
            end=Math.min(hits.length, start+hitsPerPage);
            for (int i = 0; i < end; i++) {
                System.out.println("doc="+hits[i].doc+" Score="+hits[i].score);
                Document document=searcher.doc(hits[i].doc);
                String path = document.get("path");
                if (path != null) {
                    System.out.println((i + 1) + ". " + path);
                    String theclass = document.get("theclass");
                    if (theclass != null) {
                        System.out.println("Class: " + theclass);
                    }
                } else {
                    System.out.println((i + 1) + ". "
                            + "No path for this document");
                }
            }
            if (numTotalHits>=end) {
                boolean quit=false;
                while (true) {
                    System.out.print("Press ");
                    if (start - hitsPerPage >= 0) {
                        System.out.print("(p)revious page, ");
                    }
                    if (start + hitsPerPage < numTotalHits) {
                        System.out.print("(n)ext page, ");
                    }
                    System.out
                            .println("(q)uit or enter number to jump to a page.");

                    String line = in.readLine();
                    if (line.length() == 0 || line.charAt(0) == 'q') {
                        quit = true;
                        break;
                    }
                    if (line.charAt(0) == 'p') {
                        start = Math.max(0, start - hitsPerPage);
                        break;
                    } else if (line.charAt(0) == 'n') {
                        if (start + hitsPerPage < numTotalHits) {
                            start += hitsPerPage;
                        }
                        break;
                    } else {
                        int page = Integer.parseInt(line);
                        if ((page - 1) * hitsPerPage < numTotalHits) {
                            start = (page - 1) * hitsPerPage;
                            break;
                        } else {
                            System.out.println("No such page");
                        }
                    }
                }
                if (quit) break;
                end = Math.min(numTotalHits, start + hitsPerPage);
            }
        }
    }
}
