package com.shuishou.sysmgr.ui.components;

import java.awt.Dialog;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

public class NumberTextField extends JFormattedTextField{

			
	public NumberTextField(final boolean allowDouble){
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (allowDouble){
					if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) 
							|| (c == KeyEvent.VK_DELETE) || (c == '.') || (c == '-'))) {
						getToolkit().beep();
						e.consume();
					}
					if (c == '.') {
						if (getText() != null && getText().indexOf(".") >= 0) {
							getToolkit().beep();
							e.consume();
						}
					}
				} else {
					if (!((c >= '0') && (c <= '9') || (c == '-'))) {
						getToolkit().beep();
						e.consume();
					}
				}
				if (c == '-'){
					if (getText() != null && getText().length() > 0){
						getToolkit().beep();
						e.consume();
					}
				}
			}
		});
	}
}
