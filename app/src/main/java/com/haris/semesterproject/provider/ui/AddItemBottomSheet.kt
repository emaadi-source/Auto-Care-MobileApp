package com.haris.semesterproject.provider.ui

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.haris.semesterproject.R
import com.haris.semesterproject.provider.data.ItemType
import com.haris.semesterproject.provider.data.ServiceItem

class AddItemBottomSheet : BottomSheetDialogFragment() {

    private var listener: OnItemAddedListener? = null
    private var itemType: ItemType = ItemType.PART // Default

    // Interface to send data back to Activity
    interface OnItemAddedListener {
        fun onItemAdded(item: ServiceItem)
    }

    companion object {
        fun newInstance(type: ItemType): AddItemBottomSheet {
            val fragment = AddItemBottomSheet()
            val args = Bundle()
            args.putString("TYPE", type.name)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the Activity implements the listener interface
        if (context is OnItemAddedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnItemAddedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_add_item, container, false)

        // Get Type from arguments
        val typeStr = arguments?.getString("TYPE") ?: "PART"
        itemType = ItemType.valueOf(typeStr)

        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etBrand = view.findViewById<EditText>(R.id.etBrand) // Used for Brand OR Description
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val etMeta = view.findViewById<EditText>(R.id.etMeta)   // Used for Stock OR Duration
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val btnClose = view.findViewById<ImageView>(R.id.btnClose)

        // --- DYNAMIC UI LOGIC ---
        if (itemType == ItemType.SERVICE) {
            // Setup for Services
            title.text = "Add New Service"
            etBrand.hint = "Description (e.g. Oil change and cleanup)"
            etMeta.hint = "Duration (e.g. 2 hours)"

            // Allow Text input for duration (e.g., "30 mins", "2 hrs")
            etMeta.inputType = InputType.TYPE_CLASS_TEXT

            btnAdd.text = "Add Service"
        } else {
            // Setup for Spare Parts
            title.text = "Add New Spare Part"
            etBrand.hint = "Brand (e.g. Castrol)"
            etMeta.hint = "Stock Quantity (e.g. 10)"

            // Restrict input to Numbers only for Stock
            etMeta.inputType = InputType.TYPE_CLASS_NUMBER

            btnAdd.text = "Add Spare Part"
        }

        btnClose.setOnClickListener { dismiss() }

        btnAdd.setOnClickListener {
            // 1. Validate Input
            if (etName.text.isEmpty() || etPrice.text.isEmpty()) {
                Toast.makeText(context, "Please enter Name and Price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Create Object based on Type
            val name = etName.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0

            // Logic to handle the dual-purpose fields
            val brandInput = etBrand.text.toString()
            val metaInput = etMeta.text.toString()

            val newItem = ServiceItem(
                id = System.currentTimeMillis().toString(), // Temporary ID
                name = name,
                category = category,
                price = price,
                type = itemType,
                // If it's a PART, save Brand. If Service, brand is null (or you could store description here)
                brand = if (itemType == ItemType.PART) brandInput else null,
                // If it's a PART, convert stock to Int. If Service, stock is null
                stock = if (itemType == ItemType.PART) metaInput.toIntOrNull() ?: 0 else null,
                // If it's a SERVICE, save Duration. If Part, duration is null
                duration = if (itemType == ItemType.SERVICE) metaInput else null
            )

            // 3. Send back to Activity
            listener?.onItemAdded(newItem)
            dismiss()
        }
    }
}