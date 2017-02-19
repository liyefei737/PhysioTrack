package com.drdc1.medic;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SquadStatus.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SquadStatus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SquadStatus extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_squad_status, container, false);
        VectorDrawable overallStatusGraph = (VectorDrawable)((ImageView)view.findViewById(R.id.overallStatusGraph)).getDrawable();
        VectorDrawable skinTmpGraph = (VectorDrawable)((ImageView)view.findViewById(R.id.skinTempGraph)).getDrawable();
        VectorDrawable coreTmpGraph = (VectorDrawable)((ImageView)view.findViewById(R.id.coreTempGraph)).getDrawable();
        VectorDrawable fatigueGraph = (VectorDrawable)((ImageView)view.findViewById(R.id.fatiqueLevelGraph)).getDrawable();
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.RED, Color.GREEN);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
        return view;
    }

}
