package org.wit.recipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.recipes.databinding.CardStepBinding


class StepsAdapter constructor(private var steps: MutableList<String?>) :
    RecyclerView.Adapter<StepsAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardStepBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        var step = steps[holder.adapterPosition]
        holder.bind(step)
    }

    override fun getItemCount(): Int = steps.size

    class MainHolder(private val binding: CardStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(step: String?) {
            binding.stepDescription.text = step
        }
    }
}