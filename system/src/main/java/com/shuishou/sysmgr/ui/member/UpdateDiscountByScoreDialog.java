package com.shuishou.sysmgr.ui.member;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.sysmgr.ConstantValue;
import com.shuishou.sysmgr.beans.HttpResult;
import com.shuishou.sysmgr.beans.MemberUpgrade;
import com.shuishou.sysmgr.http.HttpUtil;
import com.shuishou.sysmgr.ui.MainFrame;
import com.shuishou.sysmgr.ui.components.NumberTextField;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateDiscountByScoreDialog extends JDialog implements ActionListener {
    private final Logger logger = Logger.getLogger(UpdateDiscountByScoreDialog.class.getName());
    private MainFrame parent;
    private NumberTextField tfTargetDiscount = new NumberTextField(true);
    private NumberTextField tfFromScore = new NumberTextField(true);
    private NumberTextField tfEndScore = new NumberTextField(true);
    private JButton btnUpdate = new JButton("Update");
    private JButton btnClose = new JButton("Close");
    public UpdateDiscountByScoreDialog(MainFrame parent){
        this.parent = parent;
        setTitle("Update Discount Rate by Score");
        initUI();
    }

    private void initUI(){
        Container c = getContentPane();
        c.setLayout(new GridLayout(4, 2, 20, 20));
        c.add(new JLabel("Target Discount Rate: "));
        c.add(tfTargetDiscount);
        c.add(new JLabel("Score Value From(>=): "));
        c.add(tfFromScore);
        c.add(new JLabel("Score Value To(<): "));
        c.add(tfEndScore);
        c.add(btnUpdate);
        c.add(btnClose);
        btnUpdate.addActionListener(this);
        btnClose.addActionListener(this);
        setModal(true);
        setSize(400, 200);
        this.setLocation((int)(parent.getWidth() / 2 - this.getWidth() /2 + parent.getLocation().getX()),
                (int)(parent.getHeight() / 2 - this.getHeight() / 2 + parent.getLocation().getY()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdate){
            doUpdateRate();
        } else if (e.getSource() == btnClose){
            setVisible(false);
        }
    }

    private void doUpdateRate(){
        if (!checkValue())
            return;
        String url = "member/updatememberdiscountbyscore";
        Map<String, String> params = new HashMap<>();
        params.put("userId", MainFrame.getLoginUser().getId()+"");
        params.put("targetRate", tfTargetDiscount.getText());
        params.put("fromScore", tfFromScore.getText());
        params.put("toScore", tfEndScore.getText());
        String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params);
        if (response == null){
            logger.error("get null from server for update member discount rate by score. URL = " + url + ", param = "+ params);
            JOptionPane.showMessageDialog(this, "get null from server for update member discount rate by score. URL = " + url);
            return;
        }
        Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMD).create();
        HttpResult<Integer> result = gson.fromJson(response, new TypeToken<HttpResult<Integer>>(){}.getType());
        if (!result.success){
            logger.error("return false while update member discount rate by score. URL = " + url + ", response = "+response);
            JOptionPane.showMessageDialog(this, result.result);
            return;
        } else {
            JOptionPane.showMessageDialog(this, "Update successfully, operate records "+ result.data);
        }
    }

    private boolean checkValue(){
        try{
            Double rate = Double.parseDouble(tfTargetDiscount.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Target Discount Rate should be a number between 0-1");
            return false;
        }
        try{
            Double from = Double.parseDouble(tfFromScore.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Score Value From should be a number");
            return false;
        }
        try{
            Double to = Double.parseDouble(tfEndScore.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Score Value To should be a number");
            return false;
        }
        return true;
    }
}
