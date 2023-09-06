<template>
    <div class="container content-field">
        <div class="card">
            <div class="card-body">
                <slot></slot>
            </div>
        </div>
    </div>

    <div style="text-align: center; margin-top: 3vh;">
        <a class="record" href="https://beian.miit.gov.cn/" target="_blank">京ICP备2023022330号-1</a>
    </div>
</template>

<script>

</script>

<style scoped>
.content-field {
    margin-top: 20px;
}

.record {
    color: gray;
    text-decoration: none;
    font-size: 15px;
}
</style>