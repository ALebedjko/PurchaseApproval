import { useState } from "react";
import axios from "axios";
import { Card, Button, Form } from "react-bootstrap";

export default function PurchaseForm() {
    const [personalId, setPersonalId] = useState("");
    const [requestedAmount, setRequestedAmount] = useState("");
    const [paymentPeriodMonths, setPaymentPeriodMonths] = useState("");
    const [decision, setDecision] = useState(null);
    const [error, setError] = useState(null);
    const [validationErrors, setValidationErrors] = useState({});

    const validateForm = () => {
        let errors = {};

        if (!personalId.trim()) {
            errors.personalId = "Personal ID is required";
        } else if (!/^\d{11}$/.test(personalId)) {
            errors.personalId = "Personal ID must be exactly 11 digits";
        }

        if (!requestedAmount.trim()) {
            errors.requestedAmount = "Requested Amount is required";
        } else if (isNaN(requestedAmount) || requestedAmount <= 0) {
            errors.requestedAmount = "Amount must be a positive number";
        }

        if (!paymentPeriodMonths.trim()) {
            errors.paymentPeriodMonths = "Payment Period is required";
        } else if (isNaN(paymentPeriodMonths) || paymentPeriodMonths <= 0) {
            errors.paymentPeriodMonths = "Payment Period must be a positive number";
        }

        setValidationErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setDecision(null);

        if (!validateForm()) {
            return;
        }

        try {
            const response = await axios.post("http://localhost:8080/api/purchase/apply", {
                personalId,
                requestedAmount: parseFloat(requestedAmount),
                paymentPeriodMonths: parseInt(paymentPeriodMonths, 10),
            });

            setDecision(response.data);
        } catch (err) {
            setError("Failed to fetch decision. Please try again.");
            setDecision(null);
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
            <Card style={{ width: "400px" }} className="p-4 shadow">
                <h2 className="text-center">Purchase Application</h2>
                <Form onSubmit={handleSubmit}>
                    <Form.Group>
                        <Form.Label>Personal ID</Form.Label>
                        <Form.Control
                            type="text"
                            value={personalId}
                            onChange={(e) => setPersonalId(e.target.value)}
                            isInvalid={!!validationErrors.personalId}
                        />
                        <Form.Control.Feedback type="invalid">
                            {validationErrors.personalId}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group>
                        <Form.Label>Requested Amount (€)</Form.Label>
                        <Form.Control
                            type="number"
                            value={requestedAmount}
                            onChange={(e) => setRequestedAmount(e.target.value)}
                            isInvalid={!!validationErrors.requestedAmount}
                        />
                        <Form.Control.Feedback type="invalid">
                            {validationErrors.requestedAmount}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group>
                        <Form.Label>Payment Period (Months)</Form.Label>
                        <Form.Control
                            type="number"
                            value={paymentPeriodMonths}
                            onChange={(e) => setPaymentPeriodMonths(e.target.value)}
                            isInvalid={!!validationErrors.paymentPeriodMonths}
                        />
                        <Form.Control.Feedback type="invalid">
                            {validationErrors.paymentPeriodMonths}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Button type="submit" className="w-100 mt-3">
                        Apply
                    </Button>
                </Form>

                {error && <p className="text-danger mt-3">{error}</p>}
                {decision && (
                    <div className="mt-3 p-3 border rounded">
                        <p className="font-weight-bold">Decision:</p>
                        <p>{decision.approved ? "✅ Approved" : "❌ Denied"}</p>
                        <p>Approved Amount: €{decision.approvedAmount}</p>
                    </div>
                )}
            </Card>
        </div>
    );
}
