﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace SoftKeyboard.UI
{
	/// <summary>
	/// Interaction logic for Keyboard.xaml
	/// </summary>
	public partial class Keyboard : UserControl
	{
		public static readonly DependencyProperty ShiftProperty = DependencyProperty.Register("Shift", typeof(bool), typeof(Keyboard));
		public bool Shift
		{
			get { return (bool)this.GetValue(ShiftProperty); }
			set { this.SetValue(ShiftProperty, value); }
		}
		public Keyboard()
		{
			InitializeComponent();
		}
	}
}