package info.papdt.express.helper.ui;

import android.os.Bundle;

import com.rengwuxian.materialedittext.MaterialEditText;

import info.papdt.express.helper.R;

public class AddActivity extends AbsActivity {

	private MaterialEditText et_company, et_number;

	//918108247993

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
	}

	@Override
	protected void setUpViews() {
		et_company = (MaterialEditText) findViewById(R.id.et_company);
		et_number = (MaterialEditText) findViewById(R.id.et_number);
	}

}
