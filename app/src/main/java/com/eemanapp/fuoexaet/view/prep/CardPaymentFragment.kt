package com.eemanapp.fuoexaet.view.prep


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eemanapp.fuoexaet.databinding.FragmentCardPaymentBinding
import com.eemanapp.fuoexaet.di.Injectable
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.google.android.material.snackbar.Snackbar
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.exceptions.ChargeException
import co.paystack.android.exceptions.ExpiredAccessCodeException
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.di.ViewModelFactory
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.view.main.MainActivity
import com.eemanapp.fuoexaet.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class CardPaymentFragment : Fragment(), Injectable {

    private lateinit var binding: FragmentCardPaymentBinding
    private var user: User? = null
    private var hasPaymentCompleted = false
    private lateinit var viewModel: LoginViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val TOTAL_SYMBOLS = 19 // size of pattern 0000-0000-0000-0000
    private val TOTAL_DIGITS = 16 // max numbers of digits in pattern: 0000 x 4
    private val DIVIDER_MODULO = 5 // means divider position is every 5th symbol beginning with 1
    private val DIVIDER_POSITION =
        DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0
    private val DIVIDER = ' '

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        binding.cardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
//                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
//                    s.replace(0, s.length, buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER))
//                }
            }
        })

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
    }

    private fun verifyCardInput() {
        val number = binding.cardNumber
        val name = binding.cardName
        val mm = binding.cardMm
        val yy = binding.cardYy
        val cvv = binding.cardCvv

        if (number.text.toString().isEmpty()) {
            Snackbar.make(binding.root, "Card number cannot be blank", Snackbar.LENGTH_SHORT).show()
            number.requestFocus()
            return
        }

        if (name.text.toString().isEmpty()) {
            Snackbar.make(binding.root, "Card name cannot be blank", Snackbar.LENGTH_SHORT).show()
            name.requestFocus()
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
            number.text.toString(),
            mm.text.toString().toInt(),
            yy.text.toString().toInt(),
            cvv.text.toString(),
            name.text.toString()
        )

        if (userCard.isValid) {
            //
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
        charge.card = card
        PaystackSdk.chargeCard(activity, charge, object : Paystack.TransactionCallback {
            override fun onSuccess(transaction: Transaction?) {
                hasPaymentCompleted = true
                user?.hasUserPay = true
                user?.userPaymentRef = transaction?.reference
                updateUserPayment()
            }

            override fun beforeValidate(transaction: Transaction?) {
                hasPaymentCompleted = false
            }

            override fun onError(error: Throwable?, transaction: Transaction?) {
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
                } catch (e: ExpiredAccessCodeException) {
                    return
                }

                try {
                    Methods.showNotSuccessDialog(
                        context!!,
                        getString(R.string.error_occur),
                        error?.message
                            ?: "Unable to complete payment, error while charge the provided card"
                    )
                } catch (e: ChargeException) {
                    return
                }

                try {
                    Methods.showNotSuccessDialog(
                        context!!,
                        getString(R.string.error_occur),
                        error?.message
                            ?: "Unable to complete payment, error while charge the provided card"
                    )
                } catch (e: java.lang.Exception) {

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
