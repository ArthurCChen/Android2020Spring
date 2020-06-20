package materiallogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.thu.qinghuaquan.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class ChatsActivity extends AppCompatActivity {

    private ListView list;
    private ArrayAdapter mArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recyclervixx);
        list = (ListView) findViewById(R.id.list);
        getSupportActionBar().setTitle("聊天列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YCommunicate communicate = (YCommunicate) mArrayAdapter.getItem(position);
                startActivity(new Intent(ChatsActivity.this, ChatActivity.class)
                        .putExtra("umOth", communicate.getOth())
                        .putExtra("mOth", communicate.getOthn()));
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final YCommunicate communicate = (YCommunicate) mArrayAdapter.getItem(position);
                new AlertDialog.Builder(ChatsActivity.this)
                        .setTitle("提示")
                        .setMessage("是否确认删除聊天？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                communicate.delete(ChatsActivity.this, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        getData();
                                        Toast.makeText(ChatsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(ChatsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        BmobQuery<YCommunicate> query = new BmobQuery<>();
        query.findObjects(this, new FindListener<YCommunicate>() {
            @Override
            public void onSuccess(List<YCommunicate> list) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                Iterator<YCommunicate> communicateIterator = list.iterator();
                while (communicateIterator.hasNext()) {
                    YCommunicate communicate = communicateIterator.next();
                    if (communicate.getUn1().equals(SPUtils.getString("username"))
                            || communicate.getUn2().equals(SPUtils.getString("username"))) {
                        // nothing
                    } else {
                        communicateIterator.remove();
                    }
                }
                mArrayAdapter = new ArrayAdapter(ChatsActivity.this,
                        android.R.layout.simple_list_item_1, list);
                ChatsActivity.this.list.setAdapter(mArrayAdapter);
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}