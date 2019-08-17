package com.eemanapp.fuoexaet.model

import ir.mirrajabi.searchdialog.core.Searchable
import java.lang.Exception
import java.util.ArrayList

class Colleges(var position: Int, var name: String) : Searchable {
    override fun getTitle(): String {
        return name
    }

    companion object {

        fun fuoColleges(): ArrayList<Colleges> {
            return arrayListOf(
                Colleges(0, "College of Natural and Applied Sciences"),
                Colleges(1, "College of Management and Social Sciences")
            )
        }

        // 0 ==> College of Natural and Applied Sciences
        // 1 ==> College of Management and Social Sciences
        fun deptByCollege(college: Int): ArrayList<Dept> {
            val deptInConas = arrayListOf(
                Dept("Mathematical and Computer Sciences"),
                Dept("Biological Sciences"),
                Dept("Chemical sciences"),
                Dept("Physics, Electronics & Earth Sciences"),
                Dept("Medical Laboratory Science (BMLS)"),
                Dept("Nursing"),
                Dept("Public Health")
            )

            val deptInComas = arrayListOf(
                Dept("Accounting and Finance"),
                Dept("Business Administration"),
                Dept("Economics"),
                Dept("Mass Communication"),
                Dept("Political Science and Public Administration"),
                Dept("Sociology and Industrial Relations"),
                Dept("GNS")
            )

            return when (college) {
                0 -> deptInConas
                1 -> deptInComas
                else -> throw Exception("College not available, recognised college is 0 for CONAS and 1 for COMAS")
            }
        }

        fun hallInFUO():ArrayList<Hall>{
            return arrayListOf(
                Hall("Hall of Fame"),
                Hall("Hall of LOML")
            )

        }
    }
}

data class Dept(var name: String) : Searchable {
    override fun getTitle(): String {
        return name
    }
}

data class Hall(var name: String) : Searchable {
    override fun getTitle(): String {
        return name
    }
}