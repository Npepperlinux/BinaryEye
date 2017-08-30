package de.markusfisch.android.binaryeye.fragment

import de.markusfisch.android.binaryeye.app.addFragment
import de.markusfisch.android.binaryeye.R

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.ClipboardManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast

class ResultFragment : Fragment() {
	companion object {
		private val RESULT = "result"

		fun newInstance(content: String): Fragment {
			val args = Bundle()
			args.putString(RESULT, content)
			val fragment = ResultFragment()
			fragment.arguments = args
			return fragment
		}
	}

	private lateinit var contentView: EditText

	override fun onCreate(state: Bundle?) {
		super.onCreate(state)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			state: Bundle?): View {
		activity.setTitle(R.string.content)

		val view = inflater.inflate(
				R.layout.fragment_result,
				container,
				false)

		val content = arguments?.getString(RESULT) ?: ""
		contentView = view.findViewById<EditText>(R.id.content)
		contentView.setText(content)

		view.findViewById<View>(R.id.share).setOnClickListener { _ ->
			share(getContent())
		}

		return view
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.fragment_result, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.copy_to_clipboard -> {
				copyToClipboard(getContent())
				true
			}
			R.id.open_url -> {
				openUrl(getContent())
				true
			}
			R.id.create -> {
				addFragment(fragmentManager,
						CreateBarcodeFragment.newInstance(getContent()))
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun getContent(): String {
		return contentView.getText().toString()
	}

	private fun copyToClipboard(text: String) {
		activity ?: return

		val cm = activity.getSystemService(
				Context.CLIPBOARD_SERVICE) as ClipboardManager
		cm.setText(text)
		Toast.makeText(activity,
				R.string.put_into_clipboard,
				Toast.LENGTH_SHORT).show()
	}

	private fun openUrl(url: String) {
		if (activity == null || url.isEmpty()) {
			return
		}
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			startActivity(intent)
		} else {
			Toast.makeText(activity,
					R.string.cannot_resolve_action,
					Toast.LENGTH_SHORT).show()
		}
	}

	private fun share(text: String) {
		val intent = Intent(Intent.ACTION_SEND)
		intent.putExtra(Intent.EXTRA_TEXT, text)
		intent.setType("text/plain")
		startActivity(intent)
	}
}
