import { View, Text, TextInput, TouchableOpacity } from 'react-native';
import React, { useState } from 'react';
import { useNavigation } from '@react-navigation/native';
import { signInWithPhoneNumber, confirmCode } from './services';
import { getUserDocument } from './services';

export default function Login() {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [code, setCode] = useState('');
    const [confirm, setConfirm] = useState(null);
    const navigation = useNavigation();

    const handleSignIn = async () => {
        const result = await signInWithPhoneNumber(phoneNumber);
        if (result.success) {
            setConfirm(result.confirmation);
        }
    };

    const handleConfirmCode = async () => {
        if (!confirm) return;
        
        const result = await confirmCode(confirm, code);
        if (result.success) {
            const user = result.user;
            const userDocResult = await getUserDocument(user.uid);
            
            if (!userDocResult.exists) {
                navigation.navigate('Dashboard');
            } else {
                navigation.navigate('Details', { uid: user.uid });
            }
        }
    };

    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
            <TextInput
                placeholder="Phone Number"
                value={phoneNumber}
                onChangeText={setPhoneNumber}
                keyboardType="phone-pad"
                style={{ width: '80%', borderWidth: 1, padding: 10, marginBottom: 10 }}
            />
            <TouchableOpacity onPress={handleSignIn} style={{ backgroundColor: 'blue', padding: 10 }}>
                <Text style={{ color: 'white' }}>Send Code</Text>
            </TouchableOpacity>

            {confirm && (
                <View>
                    <TextInput
                        placeholder="Verification Code"
                        value={code}
                        onChangeText={setCode}
                        keyboardType="number-pad"
                        style={{ width: '80%', borderWidth: 1, padding: 10, marginBottom: 10 }}
                    />
                    <TouchableOpacity onPress={handleConfirmCode} style={{ backgroundColor: 'green', padding: 10 }}>
                        <Text style={{ color: 'white' }}>Confirm Code</Text>
                    </TouchableOpacity>
                </View>
            )}
        </View>
    );
}