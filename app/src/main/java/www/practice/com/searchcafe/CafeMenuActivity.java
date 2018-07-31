package www.practice.com.searchcafe;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CafeMenuActivity extends AppCompatActivity {

    private List<CafeMenu> mCafeMenus = createFakeData();

    private List<CafeMenu> createFakeData() {
        List<CafeMenu> items = new ArrayList<>();
        items.add(new CafeMenu(null, "Coffee", 3000, "Hot"));
        items.add(new CafeMenu(null, "Ice Coffee", 5000, "Cold"));
        items.add(new CafeMenu(null, "Latte", 7000, "with milk"));
        items.add(new CafeMenu(null, "Moca", 6000, "with chocolate"));
        items.add(new CafeMenu(null, "Ssanghwatang", 5500, "Best menu"));
        items.add(new CafeMenu(null, "Water", 3000, "Mineral"));
        items.add(new CafeMenu(null, "Lemon Juice", 100, "Cold"));
        return items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_menu);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_menus);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MenuAdapter(mCafeMenus));
    }

    private class MenuHolder extends RecyclerView.ViewHolder {

        private ImageView mThumbnail;
        private TextView mMenuName;
        private TextView mPrice;
        private TextView mInfo;
        private TextView mAmount;
        private ImageView mAddButton;
        private ImageView mRemoveButton;

        public MenuHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_cafe_menu, parent, false));
            mThumbnail = itemView.findViewById(R.id.image_view_menu_thumbnail);
            mMenuName = itemView.findViewById(R.id.text_view_menu_name);
            mPrice = itemView.findViewById(R.id.text_view_price);
            mInfo = itemView.findViewById(R.id.text_view_info);
            mAmount = itemView.findViewById(R.id.text_view_amount);
            mAddButton = itemView.findViewById(R.id.image_view_add_button);
            mRemoveButton = itemView.findViewById(R.id.image_view_remove_button);
        }

        public void bind(CafeMenu cafeMenu) {
            // TODO : hook up with real thumbnail image
            mMenuName.setText(cafeMenu.getName());
            mPrice.setText(String.valueOf(cafeMenu.getPrice()));
            mInfo.setText(cafeMenu.getInformation());
            mAddButton.setOnClickListener(l -> {
                int amount = cafeMenu.getAmount();
                cafeMenu.setAmount(amount + 1);
                mAmount.setText(String.valueOf(cafeMenu.getAmount()));
            });

            mRemoveButton.setOnClickListener(l -> {
                int amount = cafeMenu.getAmount();
                if (amount <= 0) {
                    Toast.makeText(CafeMenuActivity.this, "No item to be removed!", Toast.LENGTH_SHORT).show();
                } else {
                    cafeMenu.setAmount(amount - 1);
                    mAmount.setText(String.valueOf(cafeMenu.getAmount()));
                }
            });
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuHolder> {

        List<CafeMenu> mMenus;

        public MenuAdapter(List<CafeMenu> menus) {
            mMenus = menus;
        }

        @NonNull
        @Override
        public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(CafeMenuActivity.this);
            return new MenuHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
            holder.bind(mMenus.get(position));
        }

        @Override
        public int getItemCount() {
            return mMenus.size();
        }
    }
}
