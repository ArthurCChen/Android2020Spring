package materiallogin.ui.issued;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IssuedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IssuedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is an issued fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}