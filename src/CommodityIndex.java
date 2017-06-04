/**
 * Created by fay19 on 2017/6/3.
 */

public class CommodityIndex
{
    public static void main(String argc[])
    {
        Indexer indexer=new Indexer();
        indexer.run();
        Searcher searcher=new Searcher();
        try {
            searcher.search();
        }catch (Exception ex){
            System.out.print(ex.toString());
        }
    }
}
