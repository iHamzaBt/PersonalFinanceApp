package com.example.personalfinanceapp_0732;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String symbol = CurrencyHelper.getSymbol(getContext());
        if (e.getData() instanceof Transaction) {
            Transaction t = (Transaction) e.getData();
            String text;

            if ("Income".equals(t.getType())) {
                text = "Earned: +" + symbol + t.getAmount() + "\nBalance: " + symbol + (int) e.getY();
            } else if ("Investment".equals(t.getCategory())) {
                text = "Invested: -" + symbol + t.getAmount() + "\nBalance: " + symbol + (int) e.getY();
            } else {
                text = "Spent: -" + symbol + t.getAmount() + "\nBalance: " + symbol + (int) e.getY();
            }

            tvContent.setText(text);
        } else {
            tvContent.setText("Balance: " + symbol + (int) e.getY());
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 15f);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        float chartWidth = canvas.getWidth();
        float markerWidth = getWidth();
        float markerHeight = getHeight();

        float xOffset = -(markerWidth / 2f);
        float yOffset = -markerHeight - 15f;

        if (posX + xOffset + markerWidth > chartWidth - 10f) {
            xOffset = -markerWidth + 10f;
        } else if (posX + xOffset < 10f) {
            xOffset = -10f;
        }

        if (posY + yOffset < 0) {
            yOffset = 15f;
        }

        canvas.translate(posX + xOffset, posY + yOffset);
        draw(canvas);
        canvas.translate(-(posX + xOffset), -(posY + yOffset));
    }
}