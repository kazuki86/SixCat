package jp.gr.java_conf.kazuki.sixcat;

/**
 * Created by kazuki on 2015/01/30.
 */
public class ProfileDetail {
    public long key_id;
    public int sequence;
    public String value;

    public ProfileDetail(long key_id, int sequence, String value) {
        this.key_id = key_id;
        this.sequence = sequence;
        this.value = value;
    }
}
