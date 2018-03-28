
import java.util.TreeSet;

public class ChunkRecover {


    private int lastChunkNumber;
    private Integer minimunDataRead;
    private TreeSet<Integer> chunkNosRead;

    ChunkRecover(){
        chunkNosRead = new TreeSet<Integer>();
    }

    public void setLastChunkNumber(int lastChunkNumber) {
        this.lastChunkNumber = lastChunkNumber;
    }

    public void setMinimunDataRead(int minimunDataRead) {
        this.minimunDataRead = minimunDataRead;
    }

    public void addChunkRead(int chunkNo) {
        chunkNosRead.add(chunkNo);
    }

    /*public boolean areAllChunksRead() {
        if(this.minimunDataRead != null) {

        }

    }*/
}
