package com.eemanapp.fuoexaet.view.prep


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eemanapp.fuoexaet.di.Injectable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.exceptions.ChargeException
import co.paystack.android.exceptions.ExpiredAccessCodeException
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.google.android.material.snackbar.Snackbar
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.databinding.FragmentCardPaymentBinding
import com.eemanapp.fuoexaet.di.ViewModelFactory
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.view.main.MainActivity
import com.eemanapp.fuoexaet.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
const val TAG = "CardPaymentFragment"
const val AMOUNT_TO_CHARGE = 1000
class CardPaymentFragment : Fragment(), Injectable {

    private lateinit var binding: FragmentCardPaymentBinding
    private var user: User? = null
    private var hasPaymentCompleted = false
    private lateinit var viewModel: LoginViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        PaystackSdk.initialize(activity?.applicationContext)
        binding = FragmentCardPaymentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        user = arguments?.getParcelable(Constants.USER)
        watchAndFormatCard()
    }

    private fun watchAndFormatCard() {

        binding.cardMm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val cardMMLength = s.toString().length
                if (cardMMLength == 2) {
                    binding.cardYy.requestFocus()
                }
            }
        })

        binding.cardYy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val cardYYLength = s.toString().length
                if (cardYYLength == 2) {
                    binding.cardCvv.requestFocus()
                }
            }
        })

        binding.cardCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val cvv = s.toString().length
                if (cvv >= 3) {
                    binding.btnPay.apply {
                        isEnabled = true
                        isClickable = true
                        alpha = 1F
                    }
                } else {
                    binding.btnPay.apply {
                        isEnabled = false
                        isClickable = false
                        alpha = .3F
                    }
                }
            }
        })

        binding.btnPay.setOnClickListener {
            if (Methods.isNetworkAvailable(context!!)){
                verifyCardInput()
            }else{
                Snackbar.make(binding.root, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }
        }

        binding.whatCvv.setOnClickListener {

        }
    }

    private fun verifyCardInput() {
        val number = binding.cardNumber
        val mm = binding.cardMm
        val yy = binding.cardYy
        val cvv = binding.cardCvv

        if (number.text.toString().isEmpty()) {
            Snackbar.make(binding.root, "Card number cannot be blank", Snackbar.LENGTH_SHORT).show()
            number.requestFocus()
            return
        }

        if (mm.text.toString().isEmpty()) {
            Snackbar.make(binding.root, "Card month cannot be blank", Snackbar.LENGTH_SHORT).show()
            mm.requestFocus()
            return
        }

        if (yy.text.toString().isEmpty()) {
            Snackbar.make(binding.root, "Card year cannot be blank", Snackbar.LENGTH_SHORT).show()
            yy.requestFocus()
            return
        }

        if (cvv.text.toString().isEmpty()) {
            Snackbar.make(binding.root, "Card CVV cannot be blank", Snackbar.LENGTH_SHORT).show()
            cvv.requestFocus()
            return
        }

        val userCard = Card(
            number.text.toString().trim(), //4084080000000409
           mm.text.toString().toInt(), //12
           yy.text.toString().toInt(), // 20
            cvv.text.toString() //000
        )

        if (userCard.isValid) {
            //
            Methods.hideSoftKey(activity!!)
            showProgressHideBtn()
            performCharge(userCard)
        } else {
            //
            Methods.showNotSuccessDialog(
                context!!, getString(R.string.invalid_card),
                getString(R.string.invalid_card_message)
            ).setOnDismissListener {
                binding.cardNumber.setText("")
                binding.cardName.setText("")
                binding.cardMm.setText("")
                binding.cardYy.setText("")
                binding.cardCvv.setText("")
            }
        }

    }

    private fun performCharge(card: Card) {
        val charge = Charge()
        charge.amount = AMOUNT_TO_CHARGE
        charge.email = user?.email
        charge.card = card
        PaystackSdk.chargeCard(activity, charge, object : Paystack.TransactionCallback {
            override fun onSuccess(transaction: Transaction?) {
                Log.v(TAG, "Transaction Succeed ==> $transaction")
                hasPaymentCompleted = true
                user?.hasUserPay = true
                user?.userPaymentRef = transaction?.reference
                updateUserPayment()
            }

            override fun beforeValidate(transaction: Transaction?) {
                Log.v(TAG, "Transaction beforeValidate ==> $transaction")
                hasPaymentCompleted = false
            }

            override fun onError(error: Throwable?, transaction: Transaction?) {
                Log.v(TAG, "Transaction Error ==> ${transaction}")
                hasPaymentCompleted = false
                hideProgressShowBtn()
                try {
                    Methods.showNotSuccessDialog(
                        context!!,
                        getString(R.string.error_occur),
                        error?.message ?: "Unable to complete payment, " +
                        "expired access code, " +
                        "please try to making the payment "
                    )
                } catch (e: Exception) {
                    Log.v(TAG, "Transaction Exception ==> ${e.message}")
                    return
                }
            }
        })
    }

    private fun showProgressHideBtn() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnPay.visibility = View.GONE
    }

    private fun hideProgressShowBtn() {
        binding.progressBar.visibility = View.GONE
        binding.btnPay.visibility = View.VISIBLE
    }

    private fun updateUserPayment() {
        viewModel.updateUserInfo(user!!).observe(this, Observer {
            hideProgressShowBtn()
            it?.let {
                if (it.status!!) {
                    val d = Methods.showSuccessDialog(
                        context!!,
                        "Payment Completed",
                        it.message!!,
                        "Proceed"
                    )
                    d.setCancelable(false)
                    d.setConfirmClickListener {
                        startMainActivity()
                    }
                } else {
                    Methods.showNotSuccessDialog(context!!, "Error Recording Payment", it.message!!)
                }
            }
        })
    }

    private fun startMainActivity() {
        findNavController().navigate(R.id.to_mainActivity)
        activity?.finish()
    }

    override fun onDestroy() {
        if (hasPaymentCompleted) {
            super.onDestroy()
        } else {
            FirebaseAuth.getInstance().signOut()
            super.onDestroy()
        }
    }
}
