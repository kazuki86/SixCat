package jp.gr.java_conf.kazuki.sixcat;

import java.util.List;

/**
 *
 * 簡易的なバリデーションクラス
 * 複雑な仕様には耐えられない。
 *
 * Created by kazuki on 2015/01/30.
 */
public class ProfileValidator {

    public static int NO_ERROR = 0;

    private static ProfileValidator instance = null;
    private ProfileValidator() {

    }

    public static ProfileValidator getInstance() {
        if (instance == null){
            instance = new ProfileValidator();
        }
        return instance;
    }

    public int validate(List<ProfileDetail> target) {

        if (!validateAllEmpty(target)){
            return R.string.validate_message_all_empty;
        }
        return NO_ERROR;
    }

    private boolean validateAllEmpty(List<ProfileDetail> target) {
        for (ProfileDetail item : target) {
            if (item.value != null && ! item.value.isEmpty() ) {
                return true;
            }
        }
        return false;
    }
}
