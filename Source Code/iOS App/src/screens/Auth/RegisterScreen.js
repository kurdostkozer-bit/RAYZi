import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
  TouchableOpacity,
  StatusBar,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {useDispatch, useSelector} from 'react-redux';
import Icon from 'react-native-vector-icons/Ionicons';

import {Button, Input, Loading} from '../../components';
// تصحيح مسار استدعاء الخدمة المباشر لمنع أخطاء الـ undefined
import authService from '../../services/authService';
import {registerStart, registerSuccess, registerFailure} from '../../store/slices/authSlice';
import {saveToken} from '../../services/api';
import socketService from '../../services/socketService';

const RegisterScreen = ({navigation}) => {
  const dispatch = useDispatch();
  const {loading} = useSelector(state => state.auth);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [agreeToTerms, setAgreeToTerms] = useState(false);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name) {
      newErrors.name = 'الاسم مطلوب';
    } else if (formData.name.length < 3) {
      newErrors.name = 'الاسم يجب أن يكون 3 أحرف على الأقل';
    }

    if (!formData.email) {
      newErrors.email = 'البريد الإلكتروني مطلوب';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'البريد الإلكتروني غير صالح';
    }

    if (!formData.phone) {
      newErrors.phone = 'رقم الهاتف مطلوب';
    } else if (!/^\d{10,}$/.test(formData.phone)) {
      newErrors.phone = 'رقم الهاتف غير صالح';
    }

    if (!formData.password) {
      newErrors.password = 'كلمة المرور مطلوبة';
    } else if (formData.password.length < 6) {
      newErrors.password = 'كلمة المرور يجب أن تكون 6 أحرف على الأقل';
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'تأكيد كلمة المرور مطلوب';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'كلمات المرور غير متطابقة';
    }

    if (!agreeToTerms) {
      newErrors.terms = 'يجب الموافقة على الشروط والأحكام';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validateForm()) return;

    dispatch(registerStart());

    try {
      const response = await authService.register({
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        password: formData.password,
      });
      
      // حفظ التوكن الجديد في ذاكرة الجهاز المشفرة
      await saveToken(response.token);
      
      // ربط وتشغيل السوكيت التفاعلي للاعب الجديد فوراً
      try {
        await socketService.connect();
      } catch (socketError) {
        console.warn('📡 Game socket connection deferred:', socketError.message);
      }
      
      // تفعيل حالة النجاح ليتولى نظام الـ Navigation نقل اللاعب تلقائياً للرئيسية وبأمان
      dispatch(registerSuccess({
        token: response.token,
        user: response.user,
      }));

    } catch (error) {
      dispatch(registerFailure(error?.message || 'فشل إنشاء الحساب، يرجى التحقق من البيانات'));
    }
  };

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.container}>
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={styles.keyboardContainer}>
          <ScrollView
            contentContainerStyle={styles.scrollContent}
            showsVerticalScrollIndicator={false}>
            <View style={styles.header}>
              <Text style={styles.title}>إنشاء حساب جديد</Text>
              <Text style={styles.subtitle}>انضم إلينا وابدأ اللعب</Text>
            </View>

            <View style={styles.form}>
              <Input
                label="الاسم الكامل"
                placeholder="أدخل اسمك الكامل"
                value={formData.name}
                onChangeText={text => setFormData({...formData, name: text})}
                icon="person-outline"
                error={errors.name}
              />

              <Input
                label="البريد الإلكتروني"
                placeholder="أدخل بريدك الإلكتروني"
                value={formData.email}
                onChangeText={text => setFormData({...formData, email: text})}
                keyboardType="email-address"
                autoCapitalize="none"
                icon="mail-outline"
                error={errors.email}
              />

              <Input
                label="رقم الهاتف"
                placeholder="أدخل رقم هاتفك"
                value={formData.phone}
                onChangeText={text => setFormData({...formData, phone: text})}
                keyboardType="phone-pad"
                icon="call-outline"
                error={errors.phone}
              />

              <Input
                label="كلمة المرور"
                placeholder="أدخل كلمة المرور"
                value={formData.password}
                onChangeText={text => setFormData({...formData, password: text})}
                secureTextEntry={!showPassword}
                icon="lock-closed-outline"
                error={errors.password}
              />

              <Input
                label="تأكيد كلمة المرور"
                placeholder="أعد إدخال كلمة المرور"
                value={formData.confirmPassword}
                onChangeText={text => setFormData({...formData, confirmPassword: text})}
                secureTextEntry={!showConfirmPassword}
                icon="lock-closed-outline"
                error={errors.confirmPassword}
              />

              <TouchableOpacity
                style={styles.termsContainer}
                onPress={() => setAgreeToTerms(!agreeToTerms)}>
                <View style={[styles.checkbox, agreeToTerms && styles.checkboxChecked]}>
                  {agreeToTerms && <Icon name="checkmark" size={16} color="#fff" />}
                </View>
                <Text style={styles.termsText}>
                  أوافق على{' '}
                  <Text style={styles.termsLink}>الشروط والأحكام</Text>
                  {' و'}
                  <Text style={styles.termsLink}>سياسة الخصوصية</Text>
                </Text>
              </TouchableOpacity>

              {errors.terms && <Text style={styles.errorText}>{errors.terms}</Text>}

              <Button
                title="إنشاء حساب"
                onPress={handleRegister}
                loading={loading}
                style={styles.registerButton}
              />

              <View style={styles.loginContainer}>
                <Text style={styles.loginText}>لديك حساب بالفعل؟</Text>
                <TouchableOpacity onPress={() => navigation.navigate('Login')}>
                  <Text style={styles.loginLink}>تسجيل الدخول</Text>
                </TouchableOpacity>
              </View>
            </View>
          </ScrollView>
        </KeyboardAvoidingView>
      </LinearGradient>
      <Loading visible={loading} text="جاري إنشاء الحساب..." />
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  keyboardContainer: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    padding: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 30,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 10,
  },
  subtitle: {
    fontSize: 16,
    color: 'rgba(255, 255, 255, 0.9)',
  },
  form: {
    backgroundColor: '#fff',
    borderRadius: 20,
    padding: 25,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 5,
  },
  termsContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 15,
  },
  checkbox: {
    width: 20,
    height: 20,
    borderRadius: 10,
    borderWidth: 2,
    borderColor: '#ddd',
    marginRight: 10,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 2,
  },
  checkboxChecked: {
    backgroundColor: '#e60a57',
    borderColor: '#e60a57',
  },
  termsText: {
    fontSize: 13,
    color: '#666',
    flex: 1,
  },
  termsLink: {
    color: '#e60a57',
    fontWeight: '600',
  },
  errorText: {
    fontSize: 12,
    color: '#e74c3c',
    marginBottom: 15,
  },
  registerButton: {
    marginBottom: 20,
  },
  loginContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  loginText: {
    fontSize: 14,
    color: '#666',
  },
  loginLink: {
    fontSize: 14,
    color: '#e60a57',
    fontWeight: 'bold',
    marginLeft: 5,
  },
});

export default RegisterScreen;
