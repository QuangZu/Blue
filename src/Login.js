import { View, Text, TextInput, TouchableOpacity } from 'react-native';
import React, { useState } from 'react';
import auth from "@react-native-firebase/auth";
import firestore from "@react-native-firebase/firestore";
import { useNavigation } from '@react-navigation/native';

export default function Login() {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [code, setCode] = useState('');
    const [confirm, setConfirm] = useState(null);
    const navigation = useNavigation();

    const signInWithPhoneNumber = async () => {
        try {
            const confirmation = await auth().signInWithPhoneNumber(phoneNumber);
            setConfirm(confirmation);
        } catch (error) {
            console.error(error);
        }
    };

    const confirmCode = async () => {
        try {
            const userCredential = await confirm.confirm(code);
            const user = userCredential.user;

            const userDocument = await firestore().collection('users').doc(user.uid).get();
            if (!userDocument.exists) {
                navigation.navigate('Dashboard');
            } else {
                navigation.navigate('Details', { uid: user.uid });
            }
        } catch (error) {
            console.error(error);
        }
    };
    return {
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
            <TextInput
                placeholder="Phone Number"
                value={phoneNumber}
                onChangeText={setPhoneNumber}
                keyboardType="phone-pad"
                style={{ width: '80%', borderWidth: 1, padding: 10, marginBottom: 10 }}
            />
            <TouchableOpacity onPress={signInWithPhoneNumber} style={{ backgroundColor: 'blue', padding: 10 }}>
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
                    <TouchableOpacity onPress={confirmCode} style={{ backgroundColor: 'green', padding: 10 }}>
                        <Text style={{ color: 'white' }}>Confirm Code</Text>
                    </TouchableOpacity>
                </View>
            )}
        </View>
    );
    }
}