# SPay Flutter Product Flow

This document defines exactly what a consumer, merchant, and admin can do in the Flutter app. The interface is GPay-inspired in familiarity, but it uses original SPay branding and clearly simulated payment data.

## 1. Navigation Structure

The authenticated consumer app has four bottom destinations:

1. `Home`
2. `Pay`
3. `Activity`
4. `Profile`

Merchant mode appears inside Profile after the user creates a merchant profile. Admin Demo Lab appears only for an admin account or a local demo build.

## 2. App States Before Home

The app decides the next screen from backend state after login:

```text
No account -> Choose Bank
Discovery started -> Verify OTP
OTP verified -> Verify Debit Card
Bank verified, no PIN -> Set UPI PIN
PIN set, no UPI handle -> Choose UPI ID
Activation complete -> Home
```

This prevents the user from reaching payment screens before setup is complete.

## 3. Splash And Welcome

### User Sees

- SPay logo and tagline `Secure | Swift | Safe`.
- Short loading state while token and profile are checked.
- A small but visible label that SPay is a payment simulator.

### App Does

- Reads token from secure storage.
- Calls current-user summary when a token exists.
- Routes to login, activation continuation, or home.
- Clears expired or invalid token.

## 4. Register

### Fields

- Full name.
- Email.
- Phone number.
- Password.
- Confirm password.

### Actions And States

- Primary action: Create account.
- Link: Already have an account.
- Inline field validation.
- Loading state prevents double submission.
- Duplicate email or phone error appears beside the relevant field.
- Success moves to bank activation.

## 5. Login

### Fields

- Email.
- Password.

### Actions And States

- Primary action: Sign in.
- Link: Create account.
- Wrong credentials show one generic message.
- Rate-limit response shows when retry is available.
- Successful login resumes unfinished activation or opens home.

Password reset is future scope and should not be shown as a broken control.

## 6. Choose Bank

### User Sees

- Supported bank list with bank name, initials/logo treatment, and selection state.
- Registered phone number in masked form.
- Explanation that SPay will simulate finding an account linked to this phone.

### Action

- Select one bank and tap Find account.

### Results

- Loading animation simulates discovery.
- Success shows bank, masked account, IFSC, and account holder name.
- Failure offers retry.

## 7. Verify OTP

### User Sees

- Six OTP cells.
- Masked phone destination.
- Expiry countdown.
- Local/demo build displays `Demo OTP: 123456` in a clearly labeled test banner.

### Actions And States

- Verify OTP.
- Resend OTP after countdown, if implemented.
- Wrong OTP reduces remaining attempts.
- Expired OTP returns to discovery or requests a new OTP.
- Rate limit explains the retry time.

## 8. Verify Mock Debit Card

### User Sees

- Strong `Demo data only` label.
- Last six digits field.
- Expiry month and year fields.
- Statement that values are used once and never stored.

### Rules

- Do not ask for full card number or CVV.
- Do not save these values in Flutter storage.
- Clear controllers immediately after API completion.
- Demo values can be shown in local build for predictable judging.

### Result

- Success marks the linked bank account verified and reveals the demo balance.
- Failure stays on the page with a safe generic verification error.

## 9. Set UPI PIN

### User Sees

- Choose 4 or 6 digit mock UPI PIN.
- Confirm PIN.
- Numeric secure input.

### Rules

- Never store PIN in Flutter.
- Clear both fields after submission.
- Backend stores only BCrypt hash.
- Explain that this PIN works only inside SPay.

### Result

- Success moves to UPI handle setup.

## 10. Choose UPI ID

### User Sees

- Suggested handle from name, such as `ammar@spay`.
- Editable prefix and fixed `@spay` suffix.
- Live availability result after debounce.
- Linked bank summary.

### Result

- Success completes onboarding and opens Home.
- Duplicate handle suggests alternatives.

## 11. Home

### Top Area

- User greeting and profile action.
- Simulation badge so the app cannot be mistaken for a real wallet.
- Linked bank card showing bank name, masked account, UPI ID, and demo balance.
- Balance can be hidden or revealed with an eye icon.

### Primary Actions

- Send to UPI.
- Scan and Pay.
- My QR or Receive.
- Merchant Mode when enabled.

### Activity Preview

- Latest three transactions.
- Each row shows participant, sent/received direction, amount, time, and status icon.
- View all opens Activity.

### Home States

- No linked bank routes to activation.
- Frozen account shows a warning and disables payment actions.
- Offline state keeps cached public UI but disables payment submission.
- Pending transaction banner opens its detail screen.

## 12. Pay To UPI

### Step 1: Receiver

- Enter UPI ID.
- Resolve action after valid format.
- Show receiver display name, UPI ID, and masked bank on success.
- Unknown, inactive, or own UPI ID show clear errors.

### Step 2: Amount

- Large numeric rupee input.
- Optional note.
- Display configured demo limit.
- Disable continue for zero, negative, too-large, or over-balance amounts.

### Step 3: Review

- Receiver.
- Source bank and masked account.
- Amount and note.
- Primary action: Pay securely.

### Step 4: PIN Sheet

- Secure numeric UPI PIN input.
- Wrong PIN shows remaining attempts.
- Locked PIN shows lock expiry.
- Closing sheet does not create a payment.

### Submission Behavior

- Generate one UUID idempotency key when the user confirms.
- Keep that key while retrying the same network request.
- Create a new key only when the user starts a new payment intent.
- On HTTP 202, navigate to Processing with transaction ID.

## 13. Scan And Pay

### Reliable Hackathon Version

- Open a QR scanner-style page.
- Provide `Use demo merchant QR` as the guaranteed path.
- Optional camera scanner is enabled only if finished and stable.

### After Resolution

- Show merchant name, merchant UPI, fixed amount or amount input, note, and QR expiry countdown.
- Disable payment if QR expires while screen is open.
- Continue through review, PIN, processing, and receipt.

### Invalid States

- Malformed SPay payload.
- Unknown QR reference.
- Tampered signature.
- Expired or revoked QR.
- Inactive merchant.

## 14. Payment Processing

### User Sees

- Stable transaction amount and receiver.
- Animated processing indicator.
- Current status message from timeline.
- No back navigation that accidentally submits again.

### App Does

- Polls `GET /api/payments/{id}` at a controlled interval.
- Stops polling on terminal state.
- Uses capped polling duration and provides manual refresh after timeout.
- Keeps the transaction ID so reopening the app resumes status.

### Status Messages

- `INITIATED`: Payment request accepted.
- `VALIDATING`: Checking payment details.
- `PROCESSING`: Contacting the SPay simulator.
- `PENDING`: Provider did not respond; retry scheduled.
- `SUCCESS`: Payment complete.
- `FAILED`: Payment could not be completed.
- `REVERSED`: Amount returned after processing issue.
- `DEAD_LETTERED`: Payment needs admin review; no new payment should be started automatically.

## 15. Receipt

### Shared Fields

- Success, failed, pending, reversed, or dead-lettered status.
- Amount.
- Paid to or received from name.
- UPI IDs.
- Source and destination bank masks where appropriate.
- Note.
- Date and time.
- SPay transaction ID.
- Payment type.

### Actions

- Done.
- View details.
- Share receipt can be P2.
- Retry is shown only for a failed pre-processing user action; backend retrying payments are not resubmitted by the app.

## 16. Activity

### Filters

- All.
- Sent.
- Received.
- Merchant.
- Pending or failed.

### Transaction Row

- Counterparty name.
- UPI ID or merchant name.
- Direction and amount.
- Date.
- Status.

### States

- Loading skeleton.
- Empty activity.
- Pagination loading.
- API error with retry.

## 17. Transaction Detail

### Sections

- Receipt summary.
- Sender and receiver details.
- Timeline from `PaymentTimelineEvent`.
- Attempt history for local demo/admin users.
- Failure code and safe explanation when applicable.
- Transaction and idempotency references.

Attempt internals should be collapsed by default for normal consumers and expanded for the hackathon demo.

## 18. Profile And Bank Management

### User Can

- View name, email, and masked phone.
- View linked accounts and UPI handles.
- See account state and demo balance.
- Start linking another mock bank account if multi-account support is completed.
- Create merchant profile.
- Log out.

Changing UPI PIN and deactivating bank accounts are future scope unless all required features are complete.

## 19. Merchant Setup

### Fields

- Business name.
- Settlement bank account selector.
- Merchant UPI prefix.

### Rules

- Only verified accounts appear.
- Merchant UPI must be unique.
- One merchant profile per user for MVP.

### Result

- Profile gains Merchant Mode entry.

## 20. Merchant Mode

### Merchant Home

- Business name and merchant UPI ID.
- Today's received amount.
- Settlement account mask.
- Generate QR action.
- Recent received payments.

### Generate QR

- Fixed amount toggle.
- Amount when fixed.
- Optional note or order reference.
- Expiry selector with safe predefined values.
- Generate action.

### QR Display

- Large QR bitmap generated from SPay payload.
- Merchant name, amount, merchant UPI, and expiry countdown.
- Regenerate action after expiry.
- Share QR is P2.

### Received Payments

- List of payer display name, amount, time, and status.
- Payment opens the same transaction detail component.

## 21. Admin Demo Lab

Priority: P2 UI, P1 backend.

### Simulator Controls

- Segmented mode selector.
- Success and pending percentage controls only in normal mode.
- Max-attempt control.
- Save rule action.

### DLQ View

- Transaction ID, reason, retry count, and creation time.
- Detail opens attempts and timeline.
- Replay action requires confirmation.

### Operational View

- Pending outbox count.
- Failed outbox count.
- DLQ count.
- Recent simulator outcomes.

If this screen is not finished, all admin features remain fully demonstrable through Swagger.

## 22. Visual Direction

- Original SPay brand, not a pixel-for-pixel GPay clone.
- Bright neutral base with green for success, red for failure, amber for pending, and blue for actions.
- Quiet financial UI with clear hierarchy and minimal decoration.
- Cards use small corner radius and stable dimensions.
- Familiar icons for scan, send, history, bank, visibility, and settings.
- Amounts use Indian currency formatting.
- Touch targets are at least 48 logical pixels.
- All screens work on common small Android widths without clipped text.
- Status is communicated by icon plus text, never color alone.

## 23. Flutter Technical Shape

- Feature-first folders: `auth`, `bank`, `upi`, `payments`, `merchant`, `activity`, `profile`, `admin`.
- One API client with JWT interceptor and safe error mapping.
- Secure token storage.
- State management chosen from the developer's familiar stack; Riverpod or Bloc are both suitable.
- Immutable request and response models with generated JSON mapping if already familiar.
- Router guards based on authentication and activation stage.
- No UPI PIN, OTP, or debit-card verification value stored locally.
- Idempotency key retained only for the active payment submission.

## 24. Frontend Definition Of Done

The Flutter app is complete for the hackathon when a user can register, activate a mock bank account, create a UPI ID, send a UPI payment, pay a deterministic merchant QR, watch asynchronous status changes, open history and receipt, and recover gracefully from wrong PIN, invalid UPI, insufficient balance, expired QR, timeout, and network errors.
