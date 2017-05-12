package com.gentledevs.chatmodule;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gentledevs.chatmodule.dialog.MessageOptionsDialog;
import com.gentledevs.chatmodule.drawable.ChatDrawable;
import com.gentledevs.chatmodule.model.ChatDate;
import com.gentledevs.chatmodule.model.ChatItem;
import com.gentledevs.chatmodule.model.ChatMessage;
import com.gentledevs.chatmodule.model.ChatTypingItem;
import com.gentledevs.chatmodule.picasso.RoundTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemViewHolder> implements BaseChatAdapter {

    @NonNull
    private ChatAdapterOptions mOptions;

    @NonNull
    private OnLoadMoreMessagesListener mListener;
    private ForwardListener mForwardListener;
    private ImageErrorListener mImageErrorListener;

    private SimpleDateFormat mMessageTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private ArrayList<ChatItem> mItems = new ArrayList<>();

    public ChatAdapter(@NonNull ChatAdapterOptions options, @NonNull OnLoadMoreMessagesListener listener) {
        mOptions = options;
        mListener = listener;
    }

    public void setForwardListener(ForwardListener forwardListener) {
        mForwardListener = forwardListener;
    }

    public void setImageErrorListener(ImageErrorListener imageErrorListener) {
        mImageErrorListener = imageErrorListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType().ordinal();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatItem.Type type = ChatItem.Type.values()[viewType];
        switch (type) {
            case SELF:
                return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_self, parent, false));
            case OTHER:
                return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_other, parent, false));
            case DATE:
                return new DateViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_date, parent, false));
            case TYPING:
                return new TypingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_typing, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(mItems.get(position));
        if (mItems.size() - position < mOptions.getLoadingGap()) mListener.onLoadMoreMessages();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void addItem(ChatItem item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    @Override
    public void addItem(ChatItem item, int position) {
        mItems.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public void update(ChatMessage message, int position) {
        mItems.set(position, message);
        notifyItemChanged(position);
    }

    @Override
    public void addAll(List<ChatItem> items) {
        int size = mItems.size();
        mItems.addAll(items);
        notifyItemRangeInserted(size, items.size());
    }

    @Override
    public void removeItem(ChatItem item) {
        int indexOf = mItems.indexOf(item);
        if (indexOf != -1) {
            mItems.remove(indexOf);
            notifyItemRemoved(indexOf);
        }
    }

    @Override
    public void removeItem(int index) {
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public void clear() {
        int size = mItems.size();
        mItems.clear();
        notifyItemRangeRemoved(0, size);
    }

    List<ChatItem> getItems() {
        return mItems;
    }

    private class MessageViewHolder extends ItemViewHolder {

        private ChatMessage mChatMessage;

        final private ViewGroup mContentPrimary;
        final private ViewGroup mContentSecondary;
        final private ImageView mAvatar;

        MessageViewHolder(View itemView) {
            super(itemView);
            mContentPrimary = (ViewGroup) findViewById(R.id.content_primary);
            mContentSecondary = (ViewGroup) findViewById(R.id.content_secondary);
            mAvatar = (ImageView) findViewById(R.id.avatar);
        }

        @Override
        void bind(ChatItem item) {
            mChatMessage = (ChatMessage) item;

            loadImage(mAvatar, mChatMessage.getAvatarURI());

            clearViews();

            resolveContentType(mChatMessage.getContentType());
            resolveStatus(mChatMessage.getStatus());
            resolvePending(mChatMessage.isPending());
            resolveError(mChatMessage.hasError());

            setBackground(mChatMessage);

            if (mChatMessage.getType().equals(ChatItem.Type.OTHER)) setForwardListener();
            else itemView.setOnClickListener(null);
        }

        private void resolveError(boolean hasError) {
            if (!hasError) return;

            switch (mChatMessage.getContentType()) {
                case TEXT:
                    break;
                case IMAGE:
                    LayoutInflater inflater = LayoutInflater.from(getContext());

                    RelativeLayout content = (RelativeLayout) findViewById(R.id.content_secondary);
                    inflater.inflate(R.layout.error_image, content, true);

                    RelativeLayout messageLayout = (RelativeLayout) findViewById(R.id.message_layout);
                    inflater.inflate(R.layout.image_error_buttons, messageLayout, true);

                    ((RelativeLayout.LayoutParams) messageLayout.findViewById(R.id.button_container)
                            .getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);

                    ((ImageView) findViewById(R.id.image)).setColorFilter(
                            ContextCompat.getColor(getContext(), R.color.image_overlay));

                    Button buttonSend = (Button) messageLayout.findViewById(R.id.btn_send);
                    Button buttonCancel = (Button) messageLayout.findViewById(R.id.btn_cancel);

                    buttonSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mImageErrorListener != null) {
                                mImageErrorListener.onSend(mChatMessage.getId(),
                                        mChatMessage.getTempId(), mChatMessage.getText());
                            }
                        }
                    });

                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mImageErrorListener != null) {
                                mImageErrorListener.onCancel(mChatMessage.getId(),
                                        mChatMessage.getTempId());
                            }
                        }
                    });

                    break;
            }
        }

        private void resolvePending(boolean isPending) {
            removeProgress();
            if (isPending) addProgress();
        }

        private void clearViews() {
            mContentPrimary.removeAllViews();
            mContentSecondary.removeAllViews();
        }

        private void resolveStatus(ChatMessage.Status status) {
            switch (status) {
                case WAITING:
                    View inflated = LayoutInflater.from(getContext())
                            .inflate(R.layout.buttons_waiting, (ViewGroup) findViewById(R.id.content_secondary), true);
                    Button publishButton = (Button) inflated.findViewById(R.id.publish_btn);
                    publishButton.setTextColor(mOptions.getColorSelf());
                    publishButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mForwardListener) {
                                mForwardListener.onPublish(getChatMessage());
                            }
                        }
                    });

                    Button editButton = (Button) inflated.findViewById(R.id.edit_btn);
                    editButton.setTextColor(mOptions.getColorSelf());
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mForwardListener) {
                                mForwardListener.onEdit(getChatMessage());
                            }
                        }
                    });
                    break;
                case ACTIVE:
                case FORWARDED:
                case UNPUBLISHED:
                    break;
            }
        }

        private void resolveContentType(ChatMessage.ContentType contentType) {
            View inflated;
            ViewGroup content = (ViewGroup) findViewById(R.id.content_primary);
            switch (contentType) {
                case TEXT:
                    inflated = LayoutInflater.from(getContext())
                            .inflate(R.layout.message_text, content, true);
                    TextView message = (TextView) inflated.findViewById(R.id.message);
                    message.setText(mChatMessage.getText());
                    TextView time = (TextView) inflated.findViewById(R.id.time);
                    time.setText(getTimeText(mChatMessage));
                    if (mChatMessage.getType().equals(ChatItem.Type.OTHER)) {
                        setTextPadding(message);
                        setTextPadding(time);
                    }
                    break;
                case IMAGE:
                    inflated = LayoutInflater.from(getContext())
                            .inflate(R.layout.message_image, content, true);
                    ImageView image = (ImageView) inflated.findViewById(R.id.image);
                    switch (mChatMessage.getType()) {
                        case SELF:
                            image.setPadding(image.getPaddingLeft(), image.getPaddingTop(), (image.getPaddingRight() +
                                            (int) getContext().getResources().getDimension(R.dimen.chat_image_padding)),
                                    image.getPaddingBottom());
                            break;
                        case OTHER:
                            image.setPadding(image.getPaddingLeft() +
                                            (int) getContext().getResources().getDimension(R.dimen.chat_image_padding),
                                    image.getPaddingTop(), image.getPaddingRight(),
                                    image.getPaddingBottom());
                            break;
                    }

                    Transformation transformation = new RoundTransformation(
                            mChatMessage.getType().equals(ChatItem.Type.SELF),
                            getContext().getResources().getDimension(R.dimen.chat_image_corner_radius),
                            getContext().getResources().getDisplayMetrics().widthPixels);

                    if (mChatMessage.getText() != null && !mChatMessage.getText().isEmpty()) {
                        final int progressId = View.generateViewId();
                        if (!mChatMessage.isPending()) {
                            addImageLoadingProgress(progressId);
                        }
                        Picasso.with(getContext()).load(mChatMessage.getText())
                                .transform(transformation).into(image, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (mChatMessage.isPending()) return;
                                removeImageLoadingProgress(progressId);
                            }

                            @Override
                            public void onError() {
                                if (mChatMessage.isPending()) return;
                                removeImageLoadingProgress(progressId);
                            }
                        });
                    }
                    break;
            }
        }

        private void addImageLoadingProgress(int progressId) {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_primary);
            ProgressBar progressBar = new ProgressBar(getContext());
            progressBar.setId(progressId);
            progressBar.getIndeterminateDrawable().setColorFilter(
                    Color.WHITE, PorterDuff.Mode.SRC_ATOP
            );
            layout.addView(progressBar);
            progressBar.setBackgroundResource(R.drawable.round_progress);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            int size = getContext().getResources().getDimensionPixelSize(R.dimen.chat_progressbar_size);
            lp.height = size;
            lp.width = size;
        }

        private void removeImageLoadingProgress(int progressId) {
            View view = findViewById(progressId);
            if (view != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }

        private void setTextPadding(View view) {
            view.setPadding(
                    (int) (getContext().getResources().getDimension(R.dimen.chat_drawable_other_padding) +
                            view.getPaddingLeft()),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    view.getPaddingBottom());
        }

        private String getTimeText(ChatMessage message) {
            switch (message.getStatus()) {
                case FORWARDED:
                    return mMessageTimeFormat.format(message.getDate()) + ", " +
                            getContext().getString(R.string.forwarded_to_support);
                case UNPUBLISHED:
                    getContext().getString(R.string.unpublished);
                case ACTIVE:
                case WAITING:
                default:
                    return mMessageTimeFormat.format(message.getDate());
            }
        }

        private void addProgress() {
            switch (mChatMessage.getContentType()) {
                case TEXT:
                    addTextProgress();
                    break;
                case IMAGE:
                    addImageProgressBar();
                    break;
            }
        }

        private void addTextProgress() {
            LinearLayout messageLayout = (LinearLayout) findViewById(R.id.message_layout);
            RelativeLayout root = (RelativeLayout) findViewById(R.id.content_primary);

            ProgressBar progressBar = (ProgressBar) LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.message_progress_bar, root, false);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_START);
            lp.addRule(RelativeLayout.ALIGN_TOP, R.id.message_layout);
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.message_layout);

            ((RelativeLayout.LayoutParams) messageLayout.getLayoutParams())
                    .addRule(RelativeLayout.END_OF, R.id.progressBar);

            root.addView(progressBar);
        }

        private void addImageProgressBar() {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_primary);
            ProgressBar progressBar = new ProgressBar(getContext());
            progressBar.setId(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(
                    Color.WHITE, PorterDuff.Mode.SRC_ATOP
            );
            layout.addView(progressBar);
            progressBar.setBackgroundResource(R.drawable.round_progress);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            int size = getContext().getResources().getDimensionPixelSize(R.dimen.chat_progressbar_size);
            lp.height = size;
            lp.width = size;
        }

        private void removeProgress() {
            ViewGroup root = (ViewGroup) findViewById(R.id.root);

            if (root != null) {
                View view = root.findViewById(R.id.progressBar);
                root.removeView(view);
            }
        }

        private void setBackground(ChatMessage message) {

            switch (message.getContentType()) {
                case TEXT:
                    ChatDrawable drawable = new ChatDrawable();
                    switch (message.getType()) {
                        case SELF:
                            drawable.setColorFilter(mOptions.getColorSelf(), PorterDuff.Mode.SRC_ATOP);
                            drawable.setSelf(true);
                            break;
                        default:
                            drawable.setColorFilter(mOptions.getColorOther(), PorterDuff.Mode.SRC_ATOP);
                            drawable.setSelf(false);
                            break;
                    }
                    findViewById(R.id.message_layout).setBackground(drawable);
                    break;
                case IMAGE:
                    break;
            }
        }

        private void setForwardListener() {
            if (null != mForwardListener)
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mChatMessage.getType().equals(ChatItem.Type.OTHER)) {
                            return;
                        }

                        MessageOptionsDialog dialog = new MessageOptionsDialog(v.getContext(),
                                mChatMessage.getContentType().equals(ChatMessage.ContentType.TEXT));
                        dialog.setOnCopyListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager clipboard = (ClipboardManager) v.getContext()
                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText(v.getContext().getString(R.string.copied_message), mChatMessage.getText());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(v.getContext(), R.string.message_copied_to_clipboard, Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.setOnForwardListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mForwardListener != null)
                                    mForwardListener.onForward(mChatMessage);
                            }
                        });
                        dialog.show();
                    }
                });
        }

        private void loadImage(ImageView imageView, String uri) {
            if (uri != null && !uri.isEmpty()) {
                Picasso.with(getContext()).load(uri).into(imageView);
            }
        }

        ChatMessage getChatMessage() {
            return mChatMessage;
        }
    }

    private class DateViewHolder extends ItemViewHolder {

        final TextView mDate;

        DateViewHolder(View itemView) {
            super(itemView);

            mDate = (TextView) findViewById(R.id.date);
            mDate.setTextColor(mOptions.getColorTextDate());
        }

        @Override
        void bind(ChatItem item) {
            if (!(item instanceof ChatDate)) {
                return;
            }
            ChatDate date = (ChatDate) item;

            mDate.setText(getDateString(date));
        }

        private String getDateString(ChatDate chatDate) {
            switch (getDiff(chatDate.getDate())) {
                case 0:
                    return itemView.getContext().getString(R.string.today);
                case 1:
                    return itemView.getContext().getString(R.string.yesterday);
                default:
                    return new SimpleDateFormat("MMMM d", Locale.getDefault())
                            .format(chatDate.getDate()).toUpperCase(Locale.getDefault());
            }
        }

        private int getDiff(Date date) {
            return Days.daysBetween(new LocalDate(date.getTime()), new LocalDate(System.currentTimeMillis())).getDays();
        }
    }

    private class TypingViewHolder extends ItemViewHolder {

        final ImageView mImageView;

        TypingViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) findViewById(R.id.image_view);
        }

        @Override
        void bind(ChatItem item) {
            if (!(item instanceof ChatTypingItem)) {
                return;
            }
            // TODO: 4/18/17 add animation when needed
            mImageView.setImageResource(R.drawable.frame_0);
            AnimationDrawable animationDrawable = (AnimationDrawable) mImageView.getDrawable();
            animationDrawable.start();
        }
    }

    abstract class ItemViewHolder extends RecyclerView.ViewHolder {

        ItemViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bind(ChatItem item);

        View findViewById(@IdRes int id) {
            if (itemView == null) {
                throw new IllegalStateException("Should not call protected View findViewById before super(itemView");
            }
            return itemView.findViewById(id);
        }

        Context getContext() {
            if (itemView == null) {
                throw new IllegalStateException("Should not call protected View findViewById before super(itemView");
            }
            return itemView.getContext();
        }
    }

}
