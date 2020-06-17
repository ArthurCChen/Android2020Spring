package materiallogin.ui.accepted;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AcceptedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AcceptedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is an accepted fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}