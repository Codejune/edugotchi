package kr.ac.ssu.edugochi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {
    private View fragmentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation_drawer, container, false);
        fragmentView = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NavigationView navigationView = fragmentView.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav1:
                        Toast.makeText(getContext(), "No Boxes Mode", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav2:
                        Toast.makeText(getContext(), "Boxes Mode", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav3:
                        Toast.makeText(getContext(), "Text Overlay Mode", Toast.LENGTH_LONG).show();
                        break;

                }
                dismiss();
                return true;
            }
        });

        ImageView imageView = fragmentView.findViewById(R.id.close_imageview);
        imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
